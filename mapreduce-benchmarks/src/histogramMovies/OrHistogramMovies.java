package histogramMovies;
import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is an example Hadoop Map/Reduce application. It reads the input movie
 * files and outputs a histogram showing how many movies fall in which category
 * of reviews. We make 8 reduce tasks for
 * 1-1.5,1.5-2,2-2.5,2.5-3,3-3.5,3.5-4,4-4.5,4.5-5. the reduce task counts all
 * reviews in the same bin.
 *
 * To run: bin/hadoop jar build/hadoop-examples.jar histogram_movies [-m
 * <i>maps</i>] [-r <i>reduces</i>] <i>in-dir</i> <i>out-dir</i>
 *
 * @author Faraz Ahmad
 */

public class OrHistogramMovies extends Configured implements Tool {

	private enum Counter {
		WORDS, VALUES
	}

	public static class NonSplittableTextInputFormat extends TextInputFormat {
		@Override
		protected boolean isSplitable(FileSystem fs, Path file) {
			return false;
		}
	}
	public static final Log LOG = LogFactory.getLog("org.apache.hadoop.examples.HistogramMovies");

	public static class MapClass extends MapReduceBase
			implements Mapper<LongWritable, Text, FloatWritable, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private final static float division = 0.5f;

		public void map(LongWritable key, Text value, OutputCollector<FloatWritable, IntWritable> output,
				Reporter reporter) throws IOException {

			int rating, movieIndex, reviewIndex;
			int totalReviews = 0, sumRatings = 0;
			float avgReview = 0.0f, absReview, fraction, outValue = 0.0f;
			String reviews = new String();
			String line = new String();
			String tok = new String();
			String ratingStr = new String();

			line = ((Text) value).toString();
			movieIndex = line.indexOf(":");
			if (movieIndex > 0) {
				reviews = line.substring(movieIndex + 1);
				StringTokenizer token = new StringTokenizer(reviews, ",");
				while (token.hasMoreTokens()) {
					tok = token.nextToken();
					reviewIndex = tok.indexOf("_");
					ratingStr = tok.substring(reviewIndex + 1);
					rating = Integer.parseInt(ratingStr);
					sumRatings += rating;
					totalReviews++;
				}
				avgReview = (float) sumRatings / (float) totalReviews;
				absReview = (float) Math.floor((double) avgReview);

				fraction = avgReview - absReview;
				int limitInt = Math.round(1.0f / division);

				for (int i = 1; i <= limitInt; i++) {
					if (fraction < (division * i)) {
						outValue = absReview + division * i;
						break;
					}
				}
				output.collect(new FloatWritable(outValue), one);
				reporter.incrCounter(Counter.WORDS, 1);
			}
		}
	}

	public static class Reduce extends MapReduceBase
			implements Reducer<FloatWritable, IntWritable, FloatWritable, IntWritable> {

		public void reduce(FloatWritable key, Iterator<IntWritable> values,
				OutputCollector<FloatWritable, IntWritable> output, Reporter reporter) throws IOException {

			int sum = 0;
			while (values.hasNext()) {
				sum += ((IntWritable) values.next()).get();
				reporter.incrCounter(Counter.VALUES, 1);
			}
			output.collect(key, new IntWritable(sum));
		}

	}

	static void printUsage() {
		System.out.println("histogram_movies [-m <maps>] [-r <reduces>] <input> <output>");
		System.exit(1);
	}

	/**
	 * The main driver for histogram map/reduce program. Invoke this method to
	 * submit the map/reduce job.
	 * 
	 * @throws IOException
	 *             When there is communication problems with the job tracker.
	 */

	public int run(String[] args) throws Exception {

		JobConf conf = new JobConf(OrHistogramMovies.class);
		conf.setJobName("histogram_movies");
		conf.setInputFormat(NonSplittableTextInputFormat.class);
		conf.setOutputKeyClass(FloatWritable.class);
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(MapClass.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		List<String> other_args = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			try {
				if ("-m".equals(args[i])) {
					conf.setNumMapTasks(Integer.parseInt(args[++i]));
				} else if ("-r".equals(args[i])) {
					conf.setNumReduceTasks(Integer.parseInt(args[++i]));
				} else {
					other_args.add(args[i]);
				}
			} catch (NumberFormatException except) {
				System.out.println("ERROR: Integer expected instead of " + args[i]);
				printUsage();
			} catch (ArrayIndexOutOfBoundsException except) {
				System.out.println("ERROR: Required parameter missing from " + args[i - 1]);
				printUsage(); // exits
			}
		}
		if (other_args.size() < 2) {
			System.out.println("ERROR: Wrong number of parameters: " + other_args.size() + " instead of 2.");
			printUsage();
		}

		FileInputFormat.setInputPaths(conf, new Path(other_args.get(0)));
		String outPath = new String(other_args.get(1));
		FileOutputFormat.setOutputPath(conf, new Path(outPath));

		Date startTime = new Date();
		System.out.println("Job started: " + startTime);

		JobClient.runJob(conf);

		Date end_time = new Date();
		System.out.println("Job ended: " + end_time);
		System.out.println("The job took " + (end_time.getTime() - startTime.getTime()) / 1000 + " seconds.");
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new OrHistogramMovies(), args);
		System.exit(ret);
	}
}
