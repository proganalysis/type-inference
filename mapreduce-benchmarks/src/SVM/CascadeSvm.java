package SVM;

import SVM.io.TrainingSubsetInputFormat;
import libsvm.*;
// import ncku.hpds.tzulitai.mapreduce.svm.cascade.io.TrainingSubsetInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

// from here https://github.com/tzulitai/distributed-svm

/*
 * Currently only supports 2-class SVM training.
 */

public class CascadeSvm {
	
	/*
	 * PreStatCounterMapper simply counts three values:
	 * 		- TOTAL_RECORD_COUNT: The total amount of records in the whole training data set.
	 * 		- CLASS_1_COUNT: The total amount of records with label of class 1.
	 * 		- CLASS_2_COUNT: The total amount of records with label of class 2.
	 */
	
	public static class PreStatCounterMapper
		extends Mapper<Object, Text, NullWritable, Text>{
		
		public void map(Object offset, Text v_trainingData,
					    Context context) throws IOException, InterruptedException {
			context.getCounter("trainingDataStats","TOTAL_RECORD_COUNT").increment(1);
			
			String dataStr = v_trainingData.toString();
			String label = dataStr.substring(0, dataStr.indexOf(" "));
			
			// TODO: Can only recognize labels with "+1" "-1" format...
			if(label.equals("+1")){
				context.getCounter("trainingDataStats","CLASS_1_COUNT").increment(1);
			} else if(label.equals("-1")) {
				context.getCounter("trainingDataStats","CLASS_2_COUNT").increment(1);
			}
			
			context.write(NullWritable.get(), v_trainingData);
		}
	}
	
	
	/*
	 * Reads records, and randomly assign a subsetId.
	 * Reassigns a new random subsetId if the chosen subset has already collected the maximum
	 * amount of records for the corresponding label type of the record being read.
	 * 
	 * Statistical counters used for each subset:
	 * 		- SUBSET_(subsetId)_CLASS_1: The amount of records with label 1 already assigned to this subset.
	 * 		- SUBSET_(subsetId)_CLASS_2: The amount of records with label 1 already assigned to this subset.
	 */
	
	public static class PrePartitionerMapper
		extends Mapper<Object, Text, IntWritable, Text>{
		
		Random r = new Random();
		
		public void map(Object offset, Text v_trainingData,
						Context context) throws IOException, InterruptedException {
			final double subsetCount = context.getConfiguration().getInt("SUBSET_COUNT", 2);
			final double classCount_1 = context.getConfiguration().getDouble("CLASS_1_COUNT", 2);
			final double totalRecordCount = context.getConfiguration().getDouble("TOTAL_RECORD_COUNT", 2);

			final double classRatio = classCount_1/totalRecordCount;

			final double subsetMaxRecordCount = Math.ceil(totalRecordCount/subsetCount);
			final double subsetMaxClassCount_1 = Math.ceil(subsetMaxRecordCount*classRatio);
			final double subsetMaxClassCount_2 = subsetMaxRecordCount - subsetMaxClassCount_1 + 1;
			
			String dataStr = v_trainingData.toString();
			String label = dataStr.substring(0, dataStr.indexOf(" "));
			int subsetId = r.nextInt((int)subsetCount);
			
			if(label.equals("+1")) {
				while(context.getCounter("subsetDataStats", "SUBSET_" + subsetId + "_CLASS_1").getValue()
						>= subsetMaxClassCount_1)
					subsetId = r.nextInt((int)subsetCount);
				context.getCounter("subsetDataStats", "SUBSET_" + subsetId + "_CLASS_1").increment(1);
				context.write(new IntWritable(subsetId), v_trainingData);
			} else if(label.equals("-1")) {
				while(context.getCounter("subsetDataStats", "SUBSET_" + subsetId + "_CLASS_2").getValue()
						>= subsetMaxClassCount_2)
					subsetId = r.nextInt((int)subsetCount);
				context.getCounter("subsetDataStats", "SUBSET_" + subsetId + "_CLASS_2").increment(1);
				context.write(new IntWritable(subsetId), v_trainingData);
			}
		}
	}
	
	
	/*
	 * A simple identity reducer that outputs the records passed to it.
	 * Each of these reducers will be fed records that is passed to the next layer of sub-SVM training mappers.
	 * This reducer is also used in the actual cascade SVM training jobs.
	 */
	
	public static class SubsetDataOutputReducer
		extends Reducer<IntWritable, Text, NullWritable, Text>{
		
		public void reduce(IntWritable subsetId, Iterable<Text> v_subsetTrainingDataset,
				 		   Context context) throws IOException, InterruptedException {
			for(Text trainingData : v_subsetTrainingDataset) 
				 context.write(NullWritable.get(), trainingData);
		}
	}

	
	/*
	 * A refactorization of LIBSVM's implementation of reading and training a svm problem.
	 * Mostly identical to the original implementation.
	 * Used by SubSvmMapper, and LastLayerSvmModelOutputReducer.
	 */
	
	private static class SvmTrainer {
		private svm_problem prob;
		private svm_parameter param;
		private int max_index = 0;
		private String[] subsetRecords;
		
		SvmTrainer(String[] subsetRecords){
			this.subsetRecords = subsetRecords;
			formSvmProblem();
			configureSvmParameters();
		}
		
		private void formSvmProblem() {
			Vector<Double> vy = new Vector<Double>();
			Vector<svm_node[]> vx = new Vector<svm_node[]>();
			
			for(int itr=0; itr<this.subsetRecords.length; itr++) {
				StringTokenizer recordTokenItr = new StringTokenizer(this.subsetRecords[itr]," \t\n\r\f:");
				
				vy.addElement(atof(recordTokenItr.nextToken()));
				int featureCount = recordTokenItr.countTokens()/2;
				svm_node[] features = new svm_node[featureCount];
				
				// filling in the features of current record
				for(int i=0; i<featureCount; i++) {
					features[i] = new svm_node();
					features[i].index = atoi(recordTokenItr.nextToken());
					features[i].value = atof(recordTokenItr.nextToken());
				}
				
				// compare the largest feature index with max_index
				if(featureCount>0)
					this.max_index = Math.max(this.max_index, features[featureCount-1].index);
				
				vx.addElement(features);
			}
			
			this.prob = new svm_problem();
			this.prob.l = vy.size();
			
			this.prob.x = new svm_node[this.prob.l][];
			this.prob.y = new double[this.prob.l];
			for(int i=0; i<prob.l; i++) {
				this.prob.x[i] = vx.elementAt(i);
				this.prob.y[i] = vy.elementAt(i);
			}
		}
		
		private void configureSvmParameters() {
			this.param = new svm_parameter();

            		// default values
            		this.param.svm_type = svm_parameter.C_SVC;
            		this.param.kernel_type = svm_parameter.RBF;
            		this.param.degree = 3;
            		this.param.gamma = 0;        // 1/num_features
            		this.param.coef0 = 0;
            		this.param.nu = 0.5;
            		this.param.cache_size = 100;
            		this.param.C = 1;
            		this.param.eps = 1e-3;
            		this.param.p = 0.1;
            		this.param.shrinking = 1;
            		this.param.probability = 0;
            		this.param.nr_weight = 0;
            		this.param.weight_label = new int[0];
            		this.param.weight = new double[0];
            
            		if(this.param.gamma == 0 && this.max_index > 0)
                		this.param.gamma = 1.0/this.max_index;
            
            		if(this.param.kernel_type == svm_parameter.PRECOMPUTED)
                		for(int i=0;i<this.prob.l;i++) {
                        		if (this.prob.x[i][0].index != 0) {
                                		System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
                                		System.exit(1);
                        		}

                        		if ((int)this.prob.x[i][0].value <= 0 || (int)this.prob.x[i][0].value > this.max_index) {
                                		System.err.print("Wrong input format: sample_serial_number out of range\n");
                                		System.exit(1);
                        		}
                		}
		}
		
		public svm_model train(){
			svm_model model = svm.svm_train(this.prob, this.param);
			return model;
		}
	}

	
	/*
	 * A refactorization of LIBSVM's implementation of reading and training a svm problem.
	 * Mostly identical to the original implementation.
	 * Used by SubSvmMapper, and LastLayerSvmModelOutputReducer.
	 */
	
	public static class SubSvmMapper
		extends Mapper<Object, Text, IntWritable, Text>{
		
		private Text supportVector = new Text();
		private IntWritable partitionIndex = new IntWritable();
		
		public void map(Object offset, Text wholeSubset,
						Context context) throws IOException, InterruptedException {
			String[] subsetRecords = wholeSubset.toString().split("\n");
			
			SvmTrainer svmTrainer = new SvmTrainer(subsetRecords);
			svm_model model = svmTrainer.train();
            
            int[] svIndices = model.sv_indices;
            
            for(int i=0; i<svIndices.length; i++) {
            	supportVector.set(subsetRecords[svIndices[i]-1]);
            	int taskId = context.getTaskAttemptID().getTaskID().getId();
            	partitionIndex.set((int)Math.floor(taskId/2));
            	context.write(partitionIndex, supportVector);
            }
		}
	}
	

	/*
	 * This reducer will not be used for the current implementation.
	 * Reducers of each cascade layer will only act as mergers for the extracted SVs for the previous layer.
	 * Will probably use this implementation, where reducers will also train SVM models, in future version.
	 */

	public static class SubSvmReducer
		extends Reducer<IntWritable, Text, NullWritable, Text>{
		
		private Text supportVector = new Text();
		
		public void reduce(IntWritable partitionIndex, Iterable<Text> extractedSvs,
						   Context context) throws IOException, InterruptedException {
			Vector<String> svRecordsAsVector = new Vector<String>();
			for(Text sv : extractedSvs)
				svRecordsAsVector.addElement(sv.toString());
			String[] svRecords = svRecordsAsVector.toArray(new String[svRecordsAsVector.size()]);
			
			SvmTrainer svmTrainer = new SvmTrainer(svRecords);
			svm_model model = svmTrainer.train();
			
			int[] svIndices = model.sv_indices;
			
			for(int i=0; i<svIndices.length; i++) {
            			supportVector.set(svRecords[svIndices[i]-1]);
            			context.write(NullWritable.get(), supportVector);
            		}
		}
	}
	
	
	/*
	 * The last layer in the cascade, we use this reducer instead of SubsetDataOutputReducer to simply just output
	 * SVs for the next layer.
	 * LastLayerSvmModelOutputReducer also uses prints the final trained SVM model
	 * to a file in HDFS.
	 */
	
	public static class LastLayerSvmModelOutputReducer
		extends Reducer<IntWritable, Text, NullWritable, Text>{
		
		private static final String svm_type_table[] = {
	        	"c_svc","nu_svc","one_class","epsilon_svr","nu_svr",
	    	};

		static final String kernel_type_table[]= {
	                "linear","polynomial","rbf","sigmoid","precomputed"
	    	};
		
		// An identical implementation of svm.svm_save_model in LIBSVM,
		// different in that the file is saved to HDFS instead of a local path.
		private void saveModelToHdfs(svm_model model, String pathStr, Context context){
			try {
				FileSystem fs = FileSystem.get(context.getConfiguration());
				Path file = new Path(fs.getHomeDirectory(),pathStr+"/_model_file.model");
				FSDataOutputStream fos = fs.create(file);
				
				svm_parameter param = model.param;
				
				fos.writeBytes("svm_type "+svm_type_table[param.svm_type]+"\n");
				fos.writeBytes("kernel_type "+kernel_type_table[param.kernel_type]+"\n");
				
				if(param.kernel_type == svm_parameter.POLY)
					fos.writeBytes("degree "+param.degree+"\n");
				
				if(param.kernel_type == svm_parameter.POLY ||
				   param.kernel_type == svm_parameter.RBF ||
		           	   param.kernel_type == svm_parameter.SIGMOID)
					fos.writeBytes("gamma "+param.gamma+"\n");
				
				if(param.kernel_type == svm_parameter.POLY ||
		           	   param.kernel_type == svm_parameter.SIGMOID)
		            		fos.writeBytes("coef0 "+param.coef0+"\n");
				
				int nr_class = model.nr_class;
                		int l = model.l;
                		fos.writeBytes("nr_class "+nr_class+"\n");
                		fos.writeBytes("total_sv "+l+"\n");
                
                		fos.writeBytes("rho");
                		for(int i=0;i<nr_class*(nr_class-1)/2;i++)
                        		fos.writeBytes(" "+model.rho[i]);
                		fos.writeBytes("\n");
                
                		if(model.label != null) {
                			fos.writeBytes("label");
                    			for(int i=0;i<nr_class;i++)
                    				fos.writeBytes(" "+model.label[i]);
                    			fos.writeBytes("\n");
                		}
                
                		if(model.probA != null) { // regression has probA only
                    			fos.writeBytes("probA");
                    			for(int i=0;i<nr_class*(nr_class-1)/2;i++)
                    				fos.writeBytes(" "+model.probA[i]);
                    			fos.writeBytes("\n");
                		}
                
                		if(model.probB != null) {
                    			fos.writeBytes("probB");
                    			for(int i=0;i<nr_class*(nr_class-1)/2;i++)
                        			fos.writeBytes(" "+model.probB[i]);
                    			fos.writeBytes("\n");
                		}
                
                		if(model.nSV != null) {
                    			fos.writeBytes("nr_sv");
                    			for(int i=0;i<nr_class;i++)
                        			fos.writeBytes(" "+model.nSV[i]);
                    			fos.writeBytes("\n");
                		}
                
                		fos.writeBytes("SV\n");
                		double[][] sv_coef = model.sv_coef;
                		svm_node[][] SV = model.SV;
                
                		for(int i=0;i<l;i++) {
                			for(int j=0;j<nr_class-1;j++)
                				fos.writeBytes(sv_coef[j][i]+" ");

                    			svm_node[] p = SV[i];
                    			if(param.kernel_type == svm_parameter.PRECOMPUTED)
                    				fos.writeBytes("0:"+(int)(p[0].value));
                    			else
                    				for(int j=0;j<p.length;j++)
                    					fos.writeBytes(p[j].index+":"+p[j].value+" ");
                    			fos.writeBytes("\n");
                		}
                
                		fos.close();

			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
		
		private Text supportVector = new Text();
		private IntWritable partitionIndex = new IntWritable();
		
		public void reduce(IntWritable subsetId, Iterable<Text> extractedSvs,
						Context context) throws IOException, InterruptedException {
			Vector<String> svRecordsAsVector = new Vector<String>();
			for(Text sv : extractedSvs)
				svRecordsAsVector.addElement(sv.toString());
			String[] svRecords = svRecordsAsVector.toArray(new String[svRecordsAsVector.size()]);
			
			SvmTrainer svmTrainer = new SvmTrainer(svRecords);
			svm_model model = svmTrainer.train();
			
			String userOutputPathStr = context.getConfiguration().get("USER_OUTPUT_PATH");
			saveModelToHdfs(model,userOutputPathStr,context);
            
            		int[] svIndices = model.sv_indices;
            
            		for(int i=0; i<svIndices.length; i++) {
            			supportVector.set(svRecords[svIndices[i]-1]);
            			context.write(NullWritable.get(), supportVector);
            		}
		}
	}
	
	
	/*
	 * Utility functions for record passing.
	 * TODO: Should be nested functions placed elsewhere...
	 */
	
	private static double atof(String s) {
		double d = Double.valueOf(s).doubleValue();
		if (Double.isNaN(d) || Double.isInfinite(d)) {
                	System.err.print("NaN or Infinity in training data input\n");
                	System.exit(1);
        	}
		
		return d;
	}
	
	private static int atoi(String s) {
		return Integer.parseInt(s);
	}
	
	public static void main(String[] args) throws Exception {
		
		Configuration firstConf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(firstConf, args).getRemainingArgs();
		if (otherArgs.length < 2) {
			System.err.println("Usage: cascade-svm <in> <out> <subsets>");
			System.exit(2);
		}
		
		final double subsets = Double.valueOf(otherArgs[2]);
		
		if (subsets % 2 != 0){
			System.err.println("The number of subsets for a binary cascade svm should be a power of 2.");
			System.exit(2);
		}
		
		firstConf.setInt("SUBSET_COUNT",(int)subsets);	// set a Int global value
		
		final int prepartitionJobCount = 2;
		final int cascadeJobCount = (int)(Math.log(subsets)/Math.log(2));
		
		Configuration[] prepartitionConfs = new Configuration[prepartitionJobCount];
		prepartitionConfs[0] = firstConf;
		prepartitionConfs[1] = new Configuration();
		
		Configuration[] cascadeConfs = new Configuration[cascadeJobCount];
		for(int remainingConfs = 0; remainingConfs < cascadeJobCount; remainingConfs++) {
			cascadeConfs[remainingConfs] = new Configuration();
			DistributedCache.addFileToClassPath( new Path("/user/hpds/libsvm.jar"), cascadeConfs[remainingConfs]);
		}

		
		/*
		 *  Prepare prepartitioning jobs
		 */
		
		Job[] prepartitionJobs = new Job[prepartitionJobCount];
		
		prepartitionJobs[0] = new Job(prepartitionConfs[0], "Cascade SVM: Partitioning training data, Phase 1");
		prepartitionJobs[0].setJarByClass(CascadeSvm.class);
		prepartitionJobs[0].setNumReduceTasks(0);	// map-only job
		prepartitionJobs[0].setMapperClass(PreStatCounterMapper.class);
		prepartitionJobs[0].setOutputKeyClass(NullWritable.class);
		prepartitionJobs[0].setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(prepartitionJobs[0], new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(prepartitionJobs[0], new Path(otherArgs[1]+"/tmp"));
		prepartitionJobs[0].waitForCompletion(true);
		
		prepartitionConfs[1].setInt("SUBSET_COUNT",(int)subsets);
		prepartitionConfs[1].setInt("TOTAL_RECORD_COUNT",
				(int)prepartitionJobs[0].getCounters().findCounter("trainingDataStats","TOTAL_RECORD_COUNT").getValue());
		prepartitionConfs[1].setInt("CLASS_1_COUNT",
				(int)prepartitionJobs[0].getCounters().findCounter("trainingDataStats","CLASS_1_COUNT").getValue());
		prepartitionConfs[1].setInt("CLASS_2_COUNT",
				(int)prepartitionJobs[0].getCounters().findCounter("trainingDataStats","CLASS_2_COUNT").getValue());
		
		prepartitionJobs[1] = new Job(prepartitionConfs[1], "Cascade SVM: Partitioning training data, Phase 2");
		prepartitionJobs[1].setJarByClass(CascadeSvm.class);
		prepartitionJobs[1].setNumReduceTasks((int)subsets);
		prepartitionJobs[1].setMapperClass(PrePartitionerMapper.class);
		prepartitionJobs[1].setReducerClass(SubsetDataOutputReducer.class);
		prepartitionJobs[1].setMapOutputKeyClass(IntWritable.class);
		prepartitionJobs[1].setMapOutputValueClass(Text.class);
		prepartitionJobs[1].setOutputKeyClass(NullWritable.class);
		prepartitionJobs[1].setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(prepartitionJobs[1], new Path(otherArgs[1]+"/tmp"));
		FileOutputFormat.setOutputPath(prepartitionJobs[1], new Path(otherArgs[1]+"/layer-input-subsets/layer-1"));
		prepartitionJobs[1].waitForCompletion(true);
		
		
		/*
		 *  Prepare cascade jobs
		 */
		
		Job[] cascadeJobs = new Job[cascadeJobCount];
		
		// the last layer's reducer will need the output path string to write final model to HDFS
		cascadeConfs[cascadeJobCount-1].set("USER_OUTPUT_PATH", otherArgs[1]);

		for(int jobItr = 0; jobItr < cascadeJobCount; jobItr++){
			cascadeJobs[jobItr] = new Job(cascadeConfs[jobItr], "Cascade SVM: Layer " + (jobItr+1));
		
			cascadeJobs[jobItr].setJarByClass(CascadeSvm.class);
			cascadeJobs[jobItr].setNumReduceTasks((int)(subsets/Math.pow(2, jobItr+1)));
			cascadeJobs[jobItr].setMapperClass(SubSvmMapper.class);
			cascadeJobs[jobItr].setMapOutputKeyClass(IntWritable.class);
			cascadeJobs[jobItr].setMapOutputValueClass(Text.class);
			cascadeJobs[jobItr].setOutputKeyClass(NullWritable.class);
			cascadeJobs[jobItr].setOutputValueClass(Text.class);
			cascadeJobs[jobItr].setInputFormatClass(TrainingSubsetInputFormat.class);
			FileInputFormat.addInputPath(cascadeJobs[jobItr], new Path(otherArgs[1]+"/layer-input-subsets/layer-"+(jobItr+1)));
			FileOutputFormat.setOutputPath(cascadeJobs[jobItr], new Path(otherArgs[1]+"/layer-input-subsets/layer-"+(jobItr+2)));
			
			if(jobItr != cascadeJobCount-1){
				cascadeJobs[jobItr].setReducerClass(SubsetDataOutputReducer.class);
			} else {
				cascadeJobs[jobItr].setReducerClass(LastLayerSvmModelOutputReducer.class);
			}
		}

		for(int jobItr = 0; jobItr < cascadeJobCount; jobItr++){
			System.out.println("===== Beginning job for layer " + (jobItr+1) + " =====");
			if(jobItr != cascadeJobCount-1){
				cascadeJobs[jobItr].waitForCompletion(true);
			} else {
				System.exit(cascadeJobs[jobItr].waitForCompletion(true) ? 0 : 1);
			}
		}
	}
}
