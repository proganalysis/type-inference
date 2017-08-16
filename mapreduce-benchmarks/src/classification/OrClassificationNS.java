package classification;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.LongWritable;
import org.mortbay.log.Log;

public class OrClassificationNS {

	private final static int maxClusters = 16;

	private enum Counter {
		WORDS, VALUES
	}

	public static Cluster[] centroids = new Cluster[maxClusters];
	public static Cluster[] centroids_ref = new Cluster[maxClusters];

	public static String strModelFile = "hdfs://cluster-1-m:8020/user/root/initial_centroids";
	// Input data should have the following format. Each line of input record
	// represents one movie and all of its reviews.
	// Each record has the format:
	// <movie_id><:><reviewer><_><rating><,><movie_id><:><reviewer><_><rating><,>
	// .....

	public static class MapClass extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text> {
		private int totalClusters;

		public void configure(JobConf conf) {
			try {
				totalClusters = initializeCentroids();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output, Reporter reporter)
				throws IOException {
			String movieIdStr = new String();
			String reviewStr = new String();
			String userIdStr = new String();
			String reviews = new String();
			String line = new String();
			String tok = new String("");
			long movieId;
			int review, userId, p, q, r, rater, rating, movieIndex;
			int clusterId = 0;
			int[] n = new int[maxClusters];
			float[] sq_a = new float[maxClusters];
			float[] sq_b = new float[maxClusters];
			float[] numer = new float[maxClusters];
			float[] denom = new float[maxClusters];
			float max_similarity = 0.0f;
			float similarity = 0.0f;
			Cluster movie = new Cluster();

			line = ((Text) value).toString();
			movieIndex = line.indexOf(":");
			for (r = 0; r < maxClusters; r++) {
				numer[r] = 0.0f;
				denom[r] = 0.0f;
				sq_a[r] = 0.0f;
				sq_b[r] = 0.0f;
				n[r] = 0;
			}
			if (movieIndex > 0) {
				movieIdStr = line.substring(0, movieIndex);
				movieId = Long.parseLong(movieIdStr);
				movie.movie_id = movieId;
				reviews = line.substring(movieIndex + 1);
				StringTokenizer token = new StringTokenizer(reviews, ",");

				while (token.hasMoreTokens()) {
					tok = token.nextToken();
					int reviewIndex = tok.indexOf("_");
					userIdStr = tok.substring(0, reviewIndex);
					reviewStr = tok.substring(reviewIndex + 1);
					userId = Integer.parseInt(userIdStr);
					review = Integer.parseInt(reviewStr);
					for (r = 0; r < totalClusters; r++) {
						for (q = 0; q < centroids_ref[r].total; q++) {
							rater = centroids_ref[r].reviews.get(q).rater_id;
							rating = (int) centroids_ref[r].reviews.get(q).rating;
							if (userId == rater) {
								numer[r] += (float) (review * rating);
								sq_a[r] += (float) (review * review);
								sq_b[r] += (float) (rating * rating);
								n[r]++; // counter
								break; // to avoid multiple ratings by the same
										// reviewer
							}
						}
					}
				}
				for (p = 0; p < totalClusters; p++) {
					denom[p] = (float) ((Math.sqrt((double) sq_a[p])) * (Math.sqrt((double) sq_b[p])));
					if (denom[p] > 0) {
						similarity = numer[p] / denom[p];
						if (similarity > max_similarity) {
							max_similarity = similarity;
							clusterId = p;
						}
					}
				}
				output.collect(new IntWritable(clusterId), new Text(movieIdStr));
				reporter.incrCounter(Counter.WORDS, 1);
			}
		}

		public void close() {
		}
	}

	public static class Reduce extends MapReduceBase implements Reducer<IntWritable, Text, IntWritable, Text> {

		@Override
		public void reduce(IntWritable key, Iterator<Text> values, OutputCollector<IntWritable, Text> output,
				Reporter reporter) throws IOException {

			while (values.hasNext()) {
				Text cr = (Text) values.next();
				output.collect(key, cr);
				reporter.incrCounter(Counter.VALUES, 1);
			}
		}
	}

	static void printUsage() {
		System.out.println("classification [-m <maps>] [-r <reduces>] <input> <output>");
		System.exit(1);
	}

	public static int initializeCentroids() throws IOException {
		int i, k, index, numClust = 0;
		Review rv;
		String reviews = new String("");
		String SingleRv = new String("");
		Scanner opnScanner;
		for (i = 0; i < maxClusters; i++) {
			centroids[i] = new Cluster();
			centroids_ref[i] = new Cluster();
		}
		Path pt = new Path(strModelFile);
		FileSystem fs = FileSystem.get(new Configuration());
		opnScanner = new Scanner(fs.open(pt));
		while (opnScanner.hasNext()) {
			k = opnScanner.nextInt();
			centroids_ref[k].similarity = opnScanner.nextFloat();
			centroids_ref[k].movie_id = opnScanner.nextLong();
			centroids_ref[k].total = opnScanner.nextShort();
			reviews = opnScanner.next();
			@SuppressWarnings("resource")
			Scanner revScanner = new Scanner(reviews).useDelimiter(",");
			while (revScanner.hasNext()) {
				SingleRv = revScanner.next();
				index = SingleRv.indexOf("_");
				String reviewer = new String(SingleRv.substring(0, index));
				String rating = new String(SingleRv.substring(index + 1));
				rv = new Review();
				rv.rater_id = Integer.parseInt(reviewer);
				rv.rating = (byte) Integer.parseInt(rating);
				centroids_ref[k].reviews.add(rv);
			}
		}
		opnScanner.close();
		// implementing naive bubble sort as maxClusters is small
		// sorting is done to assign top most cluster ids in each iteration
		for (int pass = 1; pass < maxClusters; pass++) {
			for (int u = 0; u < maxClusters - pass; u++) {
				if (centroids_ref[u].movie_id < centroids_ref[u + 1].movie_id) {
					Cluster temp = new Cluster(centroids_ref[u]);
					centroids_ref[u] = centroids_ref[u + 1];
					centroids_ref[u + 1] = temp;
				}
			}
		}
		for (int l = 0; l < maxClusters; l++) {
			if (centroids_ref[l].movie_id != -1) {
				numClust++;
			}
		}
		return numClust;
	}

	public static int main(String[] args) throws Exception {

		int i;
		String outPath;
		int numMaps = 0, numReds = 0;

		List<String> other_args = new ArrayList<String>();
		for (i = 0; i < args.length; ++i) {
			try {
				if ("-m".equals(args[i])) {
					numMaps = Integer.parseInt(args[++i]);
				} else if ("-r".equals(args[i])) {
					numReds = Integer.parseInt(args[++i]);
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
		// Make sure there are exactly 2 parameters left.
		if (other_args.size() != 2) {
			System.out.println("ERROR: Wrong number of parameters: " + other_args.size() + " instead of 2.");
			printUsage();
		}

		Date startTime = new Date();
		Log.info("Job started: " + startTime);
		Date startIteration;
		Date endIteration;
		JobConf conf = new JobConf(OrClassificationNS.class);
		conf.setJobName("classification");
		conf.setOutputKeyClass(IntWritable.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapOutputKeyClass(IntWritable.class);
		conf.setMapOutputValueClass(Text.class);
		conf.setMapperClass(MapClass.class);
		conf.setReducerClass(Reduce.class);
		conf.setNumMapTasks(numMaps);
		conf.setNumReduceTasks(numReds);
		FileInputFormat.setInputPaths(conf, new Path(other_args.get(0)));
		outPath = new String(other_args.get(1));
		FileOutputFormat.setOutputPath(conf, new Path(outPath));
		startIteration = new Date();
		JobClient.runJob(conf);
		endIteration = new Date();
		Log.info("The iteration took " + (endIteration.getTime() - startIteration.getTime()) / 1000 + " seconds.");
		return 0;
	}
}
