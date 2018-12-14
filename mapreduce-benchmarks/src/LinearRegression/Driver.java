package LinearRegression;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierPublicKey;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.examples.terasort.TeraGen;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskCounter;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import scala.Int;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.*;

import static LinearRegression.Constants.NUM_DELIM;
import static java.lang.Math.toIntExact;

// This code came from here:d
// https://github.com/punit-naik/MLHadoop/tree/master/LinearRegression_MapReduce

// This is added because Intellij is _very_ annoying about thisd
// @SuppressWarnings("Duplicates")

public class Driver {
    public static LogWriter logWriter = new LogWriter();
	public static int num_features; // needs to be set
	public static float alpha; // needs to be set
	public static CryptoWorker cryptoWorker;
	// Got help with this from here: https://www.programcreek.com/java-api-examples/?class=org.apache.hadoop.fs.FileSystem&method=listFiles
	public static Object[] getIntermediateTheta(FileSystem hdfs, String dirname, int dimenNum, boolean USE_ENC) {
		Path path = new Path(dirname);
		Object[] theta_vals = new Object[dimenNum];
		// part-r-00010
		Pattern filenameRe = Pattern.compile(String.format("hdfs://cluster-.*/user/root/%s/part-r-\\d{5}", dirname));
		// TODO: need to add regex for non encrypted version
		Pattern fileContentRe = Pattern.compile("theta(\\d+)\\s+(\\d+#(?:-)\\d+)");
		try {
			if(hdfs.exists(path)) {
				RemoteIterator<LocatedFileStatus> fileListItr = hdfs.listFiles(path, false);
				while(fileListItr != null && fileListItr.hasNext()) {
					LocatedFileStatus file = fileListItr.next();
					Matcher filenameMatcher = filenameRe.matcher(file.getPath().toUri().toString());
					if(filenameMatcher.matches()) {
						Path filePath = file.getPath();
						FSDataInputStream inputStream = hdfs.open(filePath);
						byte[] buffer = new byte[256];
						int rc = inputStream.read(buffer);
						if(rc > 0) {
							String content = new String(buffer);
							Matcher fileContentMatcher = fileContentRe.matcher(content);
							if (fileContentMatcher.find()) {
								int theta_num = Integer.parseInt(fileContentMatcher.group(1));
								String theta_val = fileContentMatcher.group(2);
                                logWriter.write_out(String.format("Found a match! theta%d %s", theta_num, theta_val));
								if(USE_ENC) {
									theta_vals[theta_num] = cryptoWorker.cast_encrypted_number_raw_split(theta_val);
								}
								else {
									theta_vals[theta_num] = Double.parseDouble(theta_val);
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			logWriter.write_err("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return theta_vals;
	}


	public static void print_results(FileSystem hdfs, String dirname) {
		Path path = new Path(dirname);
		Pattern line_re = Pattern.compile("theta(\\d+)__final\\s+(\\d+#(?:-)\\d+)");
		ArrayList<String> theta_vals = new ArrayList<>();
		try {
			if (hdfs.exists(path)) {
				RemoteIterator<LocatedFileStatus> fileListItr = hdfs.listFiles(path, false);
				while(fileListItr != null && fileListItr.hasNext()) {
					LocatedFileStatus file = fileListItr.next();
					Path filePath = file.getPath();
					FSDataInputStream inputStream = hdfs.open(filePath);
					byte[] buffer = new byte[1024];
					int rc = inputStream.read(buffer);
					if(rc > 0) {
						String input_str = new String(buffer).trim();
						Matcher m = line_re.matcher(input_str);
						if(m.matches()) {
							int theta_num = Integer.parseInt(m.group(1)) + 1;
							String out_str = String.format("ans%d = \"%s\"", theta_num, m.group(2));
							System.out.println(out_str);
						}
					}
				}
			}
		} catch (IOException e) {
			logWriter.write_err("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static int get_input_size_bytes(FileSystem hdfs, String dirname) {
		Path path = new Path(dirname);
		int total_size = 0;
		try {
			if (hdfs.exists(path)) {
				RemoteIterator<LocatedFileStatus> fileListItr = hdfs.listFiles(path, false);
				while(fileListItr != null && fileListItr.hasNext()) {
					LocatedFileStatus file = fileListItr.next();
					Path filePath = file.getPath();
					ContentSummary cSummary = hdfs.getContentSummary(filePath);
					total_size += toIntExact(cSummary.getLength());
				}
			}
		} catch (IOException e) {
			logWriter.write_err("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return total_size;
	}

	public static void usage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Arguments (in order):\n");
		sb.append("<Number of Features>\n<Alpha Value>\n");
		sb.append("<Number of Rounds>\n<Input Path>\n");
		sb.append("<Output Path>\n<Remote Host List>\n");
		sb.append("<Remote Port>\n<Public Key>\n");
		sb.append("<Number of Inputs>\n<Number of Nodes>\n");
		sb.append("<Use encrypyion?>\n<Hide Values?>\n");
		logWriter.write_out(sb.toString());
	}

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException{
		// args[0] is the number of features each input has.
		num_features=Integer.parseInt(args[0]);
		++num_features;
		//args[1] is the value of alpha that you want to use.
		alpha=Float.parseFloat(args[1]);
		int rounds = Integer.parseInt(args[2]);
		String input_path = args[3];
		String output_path = args[4];
		String remote_host_list = args[5];
		int remote_port = Integer.parseInt(args[6]);
		String public_key = args[7];
		int number_of_inputs = Integer.parseInt(args[8]);
		int number_of_nodes = Integer.parseInt(args[9]);
		boolean use_enc = Boolean.parseBoolean(args[10]);
		boolean hide_vals = Boolean.parseBoolean(args[11]);
		Configuration conf = new Configuration();
		FileSystem hdfs = FileSystem.get(conf);
		EncryptedNumber[] theta_enc = new EncryptedNumber[num_features];
		Double[] theta = new Double[num_features];

		//rounds is the number of times you want to iterate over your training set.

		PaillierPublicKey pub_key = new PaillierPublicKey(new BigInteger(public_key));
		cryptoWorker = new CryptoWorker(pub_key, alpha, (double)number_of_inputs, remote_host_list, remote_port, hide_vals);

		for(int i = 0; i < rounds;i++){
			//for the first run
			if(i==0){
				for(int i1=0;i1<num_features;i1++){
					if(use_enc) {
						theta_enc[i1] = cryptoWorker.get_zero();
					}
					else {
						theta[i1] = 0.0;
					}
				}
			}
			//for the second run
			else{
				Object[] tmp = getIntermediateTheta(hdfs, output_path, num_features, use_enc);
				if(use_enc) {
					theta_enc = Arrays.copyOfRange(tmp, 0, tmp.length, EncryptedNumber[].class);
				}
				else {
					theta = Arrays.copyOfRange(tmp, 0, tmp.length, Double[].class);
				}
                for(int count=0; count < theta.length; count++) {
                    logWriter.write_out(String.format("Intermediate Theta%d after run %d -> %.6f", count, i-1, theta[count]));
                }
			}
			if(hdfs.exists(new Path(output_path))){
				hdfs.delete(new Path(output_path),true);
			}
			number_of_nodes = (int)Math.ceil((double)number_of_nodes * Constants.NODE_NUM_REDUCE_FACTOR);
			logWriter.write_out(String.format("Remote Host List: \'%s\'", remote_host_list));
			int total_size = get_input_size_bytes(hdfs, input_path);
			logWriter.write_out(String.format("Total size: %d", total_size));
			double max_split_size = Math.ceil(total_size / number_of_nodes);
			logWriter.write_out(String.format("MaxInputSplitSize: %d", (int)max_split_size));
			logWriter.write_out(String.format("number_of_inputs: %d", number_of_inputs));

			// passing args
			conf.set("yarn.log-aggregation-enable", "true");
			// alpha value initialisation
			conf.setFloat(Constants.ALPHA_TAG, alpha);
			conf.set(Constants.REMOTE_HOSTS_TAG, remote_host_list);
			conf.set(Constants.PUB_KEY_TAG, public_key);
			conf.setInt(Constants.REMOTE_PORT_TAG, remote_port);
			conf.setInt(Constants.NUM_INPUTS_TAG, number_of_inputs);
			conf.setBoolean(Constants.USE_ENC_TAG, use_enc);
			conf.setBoolean(Constants.HIDE_VALS_TAG, hide_vals);
			//Theta Value Initialisation
			for(int j=0;j<num_features;j++){
				if(use_enc) {
					conf.set("theta".concat(String.valueOf(j)), theta_enc[j].calculateCiphertext().toString() + NUM_DELIM + Integer.toString(theta_enc[j].getExponent()));
				}
				else {
					conf.set("theta".concat(String.valueOf(j)), String.valueOf(theta[j]));
				}
			}

			Job job = Job.getInstance(conf, "Calculation of Theta");

			// TODO: this number needs to be large enough to be well distributed  but small enough to create lots of packets
			FileInputFormat.setMaxInputSplitSize(job, (int)max_split_size);

			job.setJarByClass(Driver.class);
			FileInputFormat.setInputPaths(job, new Path(input_path));
			FileOutputFormat.setOutputPath(job, new Path(output_path));
			job.setMapperClass(thetaMAP.class);
			job.setReducerClass(thetaREDUCE.class);


			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);

			job.waitForCompletion(true);
            logWriter.write_out(String.format("Ending run %d", i));

		}
		print_results(hdfs, output_path);
        hdfs.close();
	}  
}
