/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package l15Original;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Map;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapred.jobcontrol.JobControl;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPublicKey;

import encryption.Util;

public class L15 {

	public static class ReadPageViews extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

		public void map(LongWritable k, Text val, OutputCollector<Text, Text> oc, Reporter reporter)
				throws IOException {

			// Split the line
			List<Text> fields = Library.splitLine(val, '');
			if (fields.size() != 9)
				return;

			StringBuffer sb = new StringBuffer();
			sb.append(fields.get(1).toString()); // action
			sb.append('');
			sb.append(fields.get(6).toString()); // estimated_revenue
			sb.append('');
			sb.append(fields.get(2).toString()); // timespent
			sb.append('');
			oc.collect(fields.get(0), new Text(sb.toString()));
		}
	}

	public static class Combiner extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

		private PaillierContext context;
		private PaillierPublicKey pub;
		private EncryptedNumber zero;

		@Override
		public void configure(JobConf conf) {
			String pubKey = conf.get("pubKey");
			pub = new PaillierPublicKey(new BigInteger(pubKey));
			context = pub.createSignedContext();
			zero = context.encrypt(0);
		}

		public void reduce(Text key, Iterator<Text> iter, OutputCollector<Text, Text> oc, Reporter reporter)
				throws IOException {
			HashSet<Text> hash1 = new HashSet<>();
			HashMap<String, String> hash2 = new HashMap<>();
			HashMap<String, String> hash3 = new HashMap<>();
			int cnt_per_combiner = 0;
			while (iter.hasNext()) {
				List<Text> vals = Library.splitLine(iter.next(), '');
				cnt_per_combiner++;
				hash1.add(vals.get(0));
				String val = vals.get(1).toString();
				int index = val.indexOf('&');
				if (index > -1)
					hash2.put(val.substring(0, index), val.substring(index + 1));
				else
					hash2.put("null", "");
				val = vals.get(2).toString();
				index = val.indexOf('&');
				hash3.put(val.substring(0, index), val.substring(index + 1));
			}
			EncryptedNumber rev = zero, ts = zero;
			boolean exit = false;
			for (String t : hash2.values())
				if (t.isEmpty()) {
					exit = true;
					break;
				} else
					rev = context.add(rev, Util.getAHCipher(t, context));
			if (!exit)
				for (String t : hash3.values())
					ts = context.add(ts, Util.getAHCipher(t, context));
			StringBuffer sb = new StringBuffer();
			sb.append((new Integer(hash1.size())).toString());
			sb.append("");
			sb.append(Util.getAHString(rev));
			sb.append("");
			sb.append(Util.getAHString(ts));
			sb.append("");
			sb.append(cnt_per_combiner);
			sb.append("");
			oc.collect(key, new Text(sb.toString()));
			reporter.setStatus("OK");
		}
	}

	public static class Group extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

		private int portNumber = 44444;
		private String hostName;

		@Override
		public void configure(JobConf conf) {
			hostName = conf.get("hostname");
		}

		public void reduce(Text key, Iterator<Text> iter, OutputCollector<Text, Text> oc, Reporter reporter)
				throws IOException {
			HashSet<Text> hash1 = new HashSet<Text>();
			HashSet<String> hash2 = new HashSet<>();
			HashSet<Integer> hash3 = new HashSet<>();
			Socket socket = null;
			try {
				socket = new Socket(hostName, portNumber);
			} catch (UnknownHostException e) {
				System.err.println("Don't know about host " + hostName);
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to " + hostName);
				System.exit(1);
			}
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			while (iter.hasNext()) {
				Text line = iter.next();
				List<Text> vals = Library.splitLine(line, '');
				hash1.add(vals.get(0));
				out.writeObject(vals.get(1).toString());
				String det = null;
				try {
					det = (String) in.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				hash2.add(det);
				out.writeObject(vals.get(2).toString());
				int det2 = in.readInt();
				hash3.add(det2);
			}
			out.writeObject(null);
			out.close();
			in.close();
			Double rev = 0.0;
			int ts = 0;
			for (String t : hash2)
				rev += Double.parseDouble(t);
			for (int t : hash3)
				ts += t;
			StringBuffer sb = new StringBuffer();
			sb.append((new Integer(hash1.size())).toString());
			sb.append("");
			sb.append(rev.toString());
			sb.append("");
			Double avg = (double) ts / hash3.size();
			sb.append(avg);
			oc.collect(key, new Text(sb.toString()));
			reporter.setStatus("OK");
		}
	}

	public static void main(String[] args) throws IOException {

		if (args.length != 5) {
			System.out.println("Parameters: inputDir outputDir parallel pubKey hostname");
			System.exit(1);
		}
		String inputDir = args[0];
		String outputDir = args[1];
		String parallel = args[2];
		JobConf lp = new JobConf(L15.class);
		lp.setJobName("L15 Load Page Views");
		lp.set("pubKey", args[3]);
		lp.set("hostname", args[4]);
		lp.setInputFormat(TextInputFormat.class);
		lp.setOutputKeyClass(Text.class);
		lp.setOutputValueClass(Text.class);
		lp.setMapperClass(ReadPageViews.class);
		lp.setCombinerClass(Combiner.class);
		lp.setReducerClass(Group.class);
		Properties props = System.getProperties();
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			lp.set((String) entry.getKey(), (String) entry.getValue());
		}
		FileInputFormat.addInputPath(lp, new Path(inputDir + "/page_viewsCipher"));
		FileOutputFormat.setOutputPath(lp, new Path(outputDir + "/L15out"));
		lp.setNumReduceTasks(Integer.parseInt(parallel));
		Job group = new Job(lp);

		JobControl jc = new JobControl("L15 join");
		jc.addJob(group);

		new Thread(jc).start();

		int i = 0;
		while (!jc.allFinished()) {
			ArrayList<Job> failures = jc.getFailedJobs();
			if (failures != null && failures.size() > 0) {
				for (Job failure : failures) {
					System.err.println(failure.getMessage());
				}
				break;
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}

			if (i % 10000 == 0) {
				System.out.println("Running jobs");
				ArrayList<Job> running = jc.getRunningJobs();
				if (running != null && running.size() > 0) {
					for (Job r : running) {
						System.out.println(r.getJobName());
					}
				}
				System.out.println("Ready jobs");
				ArrayList<Job> ready = jc.getReadyJobs();
				if (ready != null && ready.size() > 0) {
					for (Job r : ready) {
						System.out.println(r.getJobName());
					}
				}
				System.out.println("Waiting jobs");
				ArrayList<Job> waiting = jc.getWaitingJobs();
				if (waiting != null && waiting.size() > 0) {
					for (Job r : ready) {
						System.out.println(r.getJobName());
					}
				}
				System.out.println("Successful jobs");
				ArrayList<Job> success = jc.getSuccessfulJobs();
				if (success != null && success.size() > 0) {
					for (Job r : ready) {
						System.out.println(r.getJobName());
					}
				}
			}
			i++;
		}
		ArrayList<Job> failures = jc.getFailedJobs();
		if (failures != null && failures.size() > 0) {
			for (Job failure : failures) {
				System.err.println(failure.getMessage());
			}
		}
		jc.stop();
	}

}
