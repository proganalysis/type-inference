package LinearRegression;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This code came from here:
// https://github.com/punit-naik/MLHadoop/tree/master/LinearRegression_MapReduce

// This is added because Intellij is _very_ annoying about this
@SuppressWarnings("Duplicates")

public class Driver {
    public static LinearRegression.LogWriter logWriter;
	public static int num_features; // needs to be set
	public static float alpha; // needs to be set
	// Got help with this from here: https://www.programcreek.com/java-api-examples/?class=org.apache.hadoop.fs.FileSystem&method=listFiles
	public static float[] getIntermediateTheta(FileSystem hdfs, String dirname, int dimenNum) {
		Path path = new Path(dirname);
		float[] theta_vals = new float[dimenNum];
		// part-r-00010
		Pattern filenameRe = Pattern.compile(String.format("hdfs://cluster-.*/user/root/%s/part-r-\\d{5}", dirname));
		Pattern fileContentRe = Pattern.compile("theta(\\d+).*(\\d+\\.\\d+)");
		try {
			if(hdfs.exists(path)) {
				RemoteIterator<LocatedFileStatus> fileListItr = hdfs.listFiles(path, false);
				while(fileListItr != null && fileListItr.hasNext()) {
					LocatedFileStatus file = fileListItr.next();
					Matcher filenameMatcher = filenameRe.matcher(file.getPath().toUri().toString());
					if(filenameMatcher.matches()) {
						Path filePath = file.getPath();
                        // logWriter.write_console(String.format("%s matches!", filePath.toUri().toString()));
						FSDataInputStream inputStream = hdfs.open(filePath);
						byte[] buffer = new byte[256];
						int rc = inputStream.read(buffer);
						if(rc > 0) {
							// System.out.println(String.format("%d bytes read from %s", rc, filePath.toString()));
							String content = new String(buffer);
							Matcher fileContentMatcher = fileContentRe.matcher(content);
							if (fileContentMatcher.find()) {
								int theta_num = Integer.parseInt(fileContentMatcher.group(1));
								float theta_val = Float.parseFloat(fileContentMatcher.group(2));
                                logWriter.write_console(String.format("Found a match! theta%d %.6f", theta_num, theta_val));
								theta_vals[theta_num] = theta_val;
							}
						}
						// else {
						// 	logWriter.write_console(String.format("Didnt find a match in \'%s\'", content));
						// }
					}
					// else {
					//	logWriter.write_console(String.format("%s doesnt match", file.getPath().toUri().toString()));
					// }
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return theta_vals;
	}

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException{
        logWriter = new LinearRegression.LogWriter("Master",  LinearRegression.LogWriterType.CONSOLEWRITER);
		//args[0] is the number of features each input has.
		num_features=Integer.parseInt(args[0]);
		++num_features;
		//args[1] is the value of alpha that you want to use.
		alpha=Float.parseFloat(args[1]);
		Configuration conf=new Configuration();
		FileSystem hdfs=FileSystem.get(conf);
		float[] theta=new float[num_features];
		//args[2] is the number of times you want to iterate over your training set.
		for(int i=0;i<Integer.parseInt(args[2]);i++){
            logWriter.write_console(String.format("Starting run %d", i));
			//for the first run
			if(i==0){
				for(int i1=0;i1<num_features;i1++){
					theta[i1]= 0.0f;
				}
			}
			//for the second run
			else{
				theta = getIntermediateTheta(hdfs, args[4], num_features);
                for(int count=0; count < theta.length; count++) {
                    logWriter.write_console(String.format("Intermediate Theta%d after run %d -> %.6f", count, i-1, theta[count]));
                }
//				int iter=0;
//				//args[4] is the output path for storing the theta values.
//				BufferedReader br1 = new BufferedReader(new InputStreamReader(hdfs.open(new Path(args[4]))));
//				String line1;
//				while((line1=br1.readLine())!=null){
//					String[] theta_line = line1.split("\\\t");
//					theta[iter]=Float.parseFloat(theta_line[1]);
//					iter++;
//				}
//				br1.close();
			}
			if(hdfs.exists(new Path(args[4]))){
				hdfs.delete(new Path(args[4]),true);
			}

            logWriter.write_console(String.format("remoteAddr -> %s", args[5]));
            logWriter.write_console(String.format("numberInputs -> %s", args[6]));

			//alpha value initialisation
			conf.setFloat("alpha", alpha);
			conf.setStrings("bundle", args[5], args[6]);
			conf.set("numberInputs", args[6]);
			//Theta Value Initialisation
			for(int j=0;j<num_features;j++){
				conf.setFloat("theta".concat(String.valueOf(j)), theta[j]);
			}
			Job job = Job.getInstance(conf, "Calculation of Theta");
			// Job job = new Job(conf,"Calculation of Theta");
			job.setJarByClass(Driver.class);
			//args[3] is the input path.
			FileInputFormat.setInputPaths(job, new Path(args[3]));
			FileOutputFormat.setOutputPath(job, new Path(args[4]));
			job.setMapperClass(LinearRegression.thetaMAP.class);
			job.setReducerClass(LinearRegression.thetaREDUCE.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(FloatWritable.class);
			job.waitForCompletion(true);
            logWriter.write_console(String.format("Ending run %d", i));

		}
		theta = getIntermediateTheta(hdfs, args[4], num_features);
		for(int count=0; count < theta.length; count++) {
            logWriter.write_console(String.format("Final Theta%d %.6f", count, theta[count]));
		}
        hdfs.close();
	}  
}
