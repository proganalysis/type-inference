package kmeansModified;

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

public class OrKmeansNS {

	private final static int maxClusters = 16;

	// private static final Log LOG = LogFactory.getLog(Kmeans.class);
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

	/*
	 * The kmeans algorithm works as follows: The input data contains lists of
	 * movies with reviews by different reviewers. We need to make k clusters of
	 * similar movies. The similarity among movies is determined by the ratings
	 * given to two movies by the same reviewer. Initialization: start with k
	 * centroids, each centroid represents a movie along with the reviews. The
	 * selection of value k and the centroids for each cluster can be determined
	 * using some clustering initialization algorithms such as canopy
	 * clustering. We assume that the value of k and k centroids are
	 * pre-determined and provided to the program through local or global file
	 * system.
	 * 
	 * Map phase: Scan through input, measure similarity of all movies with the
	 * centroids, and emit <closest_centroid,<similarity, movies_data>> where
	 * movies_data is same as the input record. This movies_data needs to be
	 * propagated so that when reduce function selects a new centroid for the
	 * next iteration, it can attach the corresponding reviews with the
	 * centroid.
	 * 
	 * Reduce phase: Collect data from map phase pertaining to one centroid and
	 * compute a new centroid by averaging the similarity values for all movies
	 * in the cluster. The new centroid is selected as a movie whose similarity
	 * is closest to the average similarity value of the cluster.
	 * 
	 */

	public static class MapClass extends MapReduceBase
			implements Mapper<LongWritable, Text, IntWritable, ClusterWritable> {
		private int totalClusters;

		public void configure(JobConf conf) {
			try {
				totalClusters = initializeCentroids();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void map(LongWritable key, Text value, OutputCollector<IntWritable, ClusterWritable> output,
				Reporter reporter) throws IOException {

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
			ClusterWritable movies_arrl = new ClusterWritable();

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

				//movies_arrl.movies.add(line);
				movies_arrl.movies.add(movieIdStr);
				movies_arrl.similarities.add(max_similarity);
				movies_arrl.similarity = max_similarity;
				output.collect(new IntWritable(clusterId), movies_arrl);
				reporter.incrCounter(Counter.WORDS, 1);
			}
		}

		public void close() {
		}
	}

	public static class Reduce extends MapReduceBase
			implements Reducer<IntWritable, ClusterWritable, IntWritable, Text> {

		@Override
		public void reduce(IntWritable key, Iterator<ClusterWritable> values, OutputCollector<IntWritable, Text> output,
				Reporter reporter) throws IOException {

			float sumSimilarity = 0.0f;
			int numMovies = 0;
			float avgSimilarity = 0.0f;
			float similarity = 0.0f;
			int s = 0;
			//int count;
			float diff = 0.0f;
			float minDiff = 1.0f;
			int candidate = 0;
			String data = new String("");
			String shortline = new String("");
			ArrayList<String> arrl = new ArrayList<String>();
			ArrayList<Float> simArrl = new ArrayList<Float>();
			String oneElm = new String();
			int indexShort;//, index2;
			Text val = new Text();

			while (values.hasNext()) {
				ClusterWritable cr = (ClusterWritable) values.next();
				similarity = cr.similarity;
				simArrl.addAll(cr.similarities);
				for (int i = 0; i < cr.movies.size(); i++) {
					oneElm = cr.movies.get(i);
					indexShort = oneElm.indexOf(",", 1000); // to avoid memory
															// error caused by
															// long arrays; it
															// will results less
															// accurate
					if (indexShort == -1) {
						shortline = new String(oneElm);
					} else {
						shortline = new String(oneElm.substring(0, indexShort));
					}
					arrl.add(shortline);
					//output.collect(key, new Text(oneElm));
				}
				numMovies += cr.movies.size();
				sumSimilarity += similarity;
			}
			if (numMovies > 0) {
				avgSimilarity = sumSimilarity / (float) numMovies;
			}
			diff = 0.0f;
			minDiff = 1.0f;
			for (s = 0; s < numMovies; s++) {
				diff = (float) Math.abs(avgSimilarity - simArrl.get(s));
				if (diff < minDiff) {
					minDiff = diff;
					candidate = s;
				}
			}
			data = arrl.get(candidate);
//			index2 = data.indexOf(":");
//			String movieStr = data.substring(0, index2);
//			String reviews = data.substring(index2 + 1);
//			StringTokenizer token = new StringTokenizer(reviews, ",");
//			count = 0;
//			while (token.hasMoreTokens()) {
//				token.nextToken();
//				count++;
//			}
			Log.info("The key = " + key.toString() + " has members = " + numMovies + " simil = "
					+ simArrl.get(candidate));
			//Yao: val = new Text(simArrl.get(candidate) + " " + movieStr + " " + count + " " + reviews);
			val = new Text(simArrl.get(candidate) + " " + data);
			output.collect(key, val);
			reporter.incrCounter(Counter.VALUES, 1);

		}
	}

	static void printUsage() {
		System.out.println("kmeans [-m <maps>] [-r <reduces>] <input> <output>");
		System.exit(1);
	}

	public static int initializeCentroids() throws IOException {
		int i, k, index, numClust = 0;
		Review rv;
		String reviews = new String();
		String singleRv = new String();
		String reviewer = new String();
		String rating = new String();
		for (i = 0; i < maxClusters; i++) {
			centroids[i] = new Cluster();
			centroids_ref[i] = new Cluster();
		}
		Path pt = new Path(strModelFile);
		FileSystem fs = FileSystem.get(new Configuration());
		Scanner opnScanner = new Scanner(fs.open(pt));
		while (opnScanner.hasNext()) {
			k = opnScanner.nextInt();
			centroids_ref[k].similarity = opnScanner.nextFloat();
			centroids_ref[k].movie_id = opnScanner.nextLong();
			centroids_ref[k].total = opnScanner.nextShort();
			reviews = opnScanner.next();
			@SuppressWarnings("resource")
			Scanner revScanner = new Scanner(reviews).useDelimiter(",");
			while (revScanner.hasNext()) {
				singleRv = revScanner.next();
				index = singleRv.indexOf("_");
				reviewer = new String(singleRv.substring(0, index));
				rating = new String(singleRv.substring(index + 1));
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
		JobConf conf = new JobConf(OrKmeansNS.class);
		conf.setJobName("kmeans");
		conf.set("mapreduce.reduce.shuffle.input.buffer.percent", "0.2");
		conf.setLong("mapred.task.timeout", 1000*60*60);
		conf.setOutputKeyClass(IntWritable.class);
		conf.setOutputValueClass(Text.class);
		conf.setMapOutputKeyClass(IntWritable.class);
		conf.setMapOutputValueClass(ClusterWritable.class);
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
