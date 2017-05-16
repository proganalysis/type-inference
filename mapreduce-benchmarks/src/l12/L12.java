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
package l12;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
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

public class L12 {

    public static class HighestValuePagePerUser extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, Text>,
        Reducer<Text, Text, Text, Text> {

    	private BigInteger min;
    	private String opeZeroStr;
    	
    	@Override
    	public void configure(JobConf conf) {
    		opeZeroStr = conf.get("opeZero");
    		String minStr = conf.get("min");
    		min = new BigInteger(minStr);
    	}
    	
    	public void map(
                LongWritable k,
                Text val,
                OutputCollector<Text, Text> oc,
                Reporter reporter) throws IOException {

            List<Text> fields = Library.splitLine(val, '');

            // Filter out null users or query terms.
            if (fields.get(0).getLength() == 0 ||
                    fields.get(3).getLength() == 0) return;
            Text f6 = fields.get(6);
            if (f6.getLength() == 0)
            	oc.collect(fields.get(0), new Text(opeZeroStr));
            else
                oc.collect(fields.get(0), fields.get(6));
        }

        public void reduce(
                Text key,
                Iterator<Text> iter, 
                OutputCollector<Text, Text> oc,
                Reporter reporter) throws IOException {
        	BigInteger max = min;
            while (iter.hasNext()) {
                BigInteger d = new BigInteger(iter.next().toString());
                if (max.compareTo(d) < 0) max = d;
            }
            oc.collect(key, new Text(max.toString()));
        }
    }

    public static class TotalTimespentPerTerm extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, Text>,
        Reducer<Text, Text, Text, Text> {

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

		public void map(
                LongWritable k,
                Text val,
                OutputCollector<Text, Text> oc,
                Reporter reporter) throws IOException {
            List<Text> fields = Library.splitLine(val, '');

            // Filter out non-null users
            if (fields.get(0).getLength() != 0) return;
            oc.collect(fields.get(3), fields.get(2));
        }

        public void reduce(
                Text key,
                Iterator<Text> iter, 
                OutputCollector<Text, Text> oc,
                Reporter reporter) throws IOException {
        	EncryptedNumber sum = zero;
            while (iter.hasNext()) {
            	EncryptedNumber v = Util.getAHCipher(iter.next().toString(), context);
            	sum = context.add(sum, v);
            }
            oc.collect(key, new Text(Util.getAHString(sum)));
        }
    }

    public static class QueriesPerAction extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, LongWritable>,
        Reducer<Text, LongWritable, Text, LongWritable> {

        public void map(
                LongWritable k,
                Text val,
                OutputCollector<Text, LongWritable> oc,
                Reporter reporter) throws IOException {
            List<Text> fields = Library.splitLine(val, '');
            
            // Filter out non-null users and non-null queries
            if (fields.get(0).getLength() == 0 || fields.get(3).getLength() != 0) return;
            oc.collect(fields.get(1), new LongWritable(1));
       }

        public void reduce(
                Text key,
                Iterator<LongWritable> iter, 
                OutputCollector<Text, LongWritable> oc,
                Reporter reporter) throws IOException {
  
        	long cnt = 0;
            while (iter.hasNext()) {
                LongWritable l = iter.next();
            	cnt += l.get();
            }
            oc.collect(key, new LongWritable(cnt));
        }
    }


    public static void main(String[] args) throws IOException {

        if (args.length!=6) {
            System.out.println("Parameters: inputDir outputDir parallel opeZero min pubKey");
            System.exit(1);
        }
        String inputDir = args[0];
        String outputDir = args[1];
        String parallel = args[2];
        JobConf lp = new JobConf(L12.class);
        lp.setJobName("L12 Find Highest Value Page Per User");
        lp.set("opeZero", args[3]);
        lp.set("min", args[4]);
        lp.setInputFormat(TextInputFormat.class);
        lp.setOutputKeyClass(Text.class);
        lp.setOutputValueClass(Text.class);
        lp.setMapperClass(HighestValuePagePerUser.class);
        lp.setCombinerClass(HighestValuePagePerUser.class);
        lp.setReducerClass(HighestValuePagePerUser.class);
        Properties props = System.getProperties();
        for (Map.Entry<Object,Object> entry : props.entrySet()) {
            lp.set((String)entry.getKey(), (String)entry.getValue());
        }
        FileInputFormat.addInputPath(lp, new Path(inputDir + "/page_viewsCipher"));
        FileOutputFormat.setOutputPath(lp, new Path(outputDir + "/highest_value_page_per_user"));
        lp.setNumReduceTasks(Integer.parseInt(parallel));
        Job loadPages = new Job(lp);

        JobConf lu = new JobConf(L12.class);
        lu.setJobName("L12 Find Total Timespent per Term");
        lu.set("pubKey", args[5]);
        lu.setInputFormat(TextInputFormat.class);
        lu.setOutputKeyClass(Text.class);
        lu.setOutputValueClass(Text.class);
        lu.setMapperClass(TotalTimespentPerTerm.class);
        lu.setCombinerClass(TotalTimespentPerTerm.class);
        lu.setReducerClass(TotalTimespentPerTerm.class);
        props = System.getProperties();
        for (Map.Entry<Object,Object> entry : props.entrySet()) {
            lu.set((String)entry.getKey(), (String)entry.getValue());
        }
        FileInputFormat.addInputPath(lu, new Path(inputDir + "/page_viewsCipher"));
        FileOutputFormat.setOutputPath(lu, new Path(outputDir + "/total_timespent_per_term"));
        lu.setNumReduceTasks(Integer.parseInt(parallel));
        Job loadUsers = new Job(lu);

        JobConf join = new JobConf(L12.class);
        join.setJobName("L12 Find Queries Per Action");
        join.setInputFormat(TextInputFormat.class);
        join.setOutputKeyClass(Text.class);
        join.setOutputValueClass(LongWritable.class);
        join.setMapperClass(QueriesPerAction.class);
        join.setCombinerClass(QueriesPerAction.class);
        join.setReducerClass(QueriesPerAction.class);
        props = System.getProperties();
        for (Map.Entry<Object,Object> entry : props.entrySet()) {
            join.set((String)entry.getKey(), (String)entry.getValue());
        }
        FileInputFormat.addInputPath(join, new Path(inputDir + "/page_viewsCipher"));
        FileOutputFormat.setOutputPath(join, new Path(outputDir + "/queries_per_action"));
        join.setNumReduceTasks(Integer.parseInt(parallel));
        Job joinJob = new Job(join);

        JobControl jc = new JobControl("L12 join");
        jc.addJob(loadPages);
        jc.addJob(loadUsers);
        jc.addJob(joinJob);

        new Thread(jc).start();
   
        int i = 0;
        while(!jc.allFinished()){
            ArrayList<Job> failures = jc.getFailedJobs();
            if (failures != null && failures.size() > 0) {
                for (Job failure : failures) {
                    System.err.println(failure.getMessage());
                }
                break;
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}

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
