package MutualFriends;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

// Taken from here: https://github.com/punit-naik/MLHadoop

public class MF_Driver {


	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

		System.out.println("Started successfully");

		Configuration conf=new Configuration();
		Job job = new Job(conf);
		job.setJarByClass(MF_Driver.class);
		job.setJobName("Mutual Friend Calculator");
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(MutualFriends.MF_Mapper.class);
		job.setCombinerClass(MutualFriends.MF_Reducer.class);
		job.setReducerClass(MutualFriends.MF_Reducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		boolean success = job.waitForCompletion(true);

		System.out.println("It worked?");

		System.exit(success ? 0 : 1);
	};

}
