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
package l17;

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

public class L17 {

    public static class ReadPageViews extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, Text> {

        public void map(
                LongWritable k,
                Text val,
                OutputCollector<Text, Text> oc,
                Reporter reporter) throws IOException {
            List<Text> vals = Library.splitLine(val, '');
            if (vals.size() != 27) return;
            StringBuffer key = new StringBuffer();
            key.append(vals.get(0).toString());
            key.append("");
            key.append(vals.get(1).toString());
            key.append("");
            String f2 = vals.get(2).toString();
            int i2 = f2.indexOf('&');
            key.append(f2.substring(0, i2));
            key.append("");
            key.append(vals.get(3).toString());
            key.append("");
            key.append(vals.get(4).toString());
            key.append("");
            key.append(vals.get(5).toString());
            key.append("");
            String f6 = vals.get(6).toString();
            int i6 = f6.indexOf('&');
            if (i6 > -1)
            	key.append(f6.substring(0, i6));
            key.append("");
            key.append(vals.get(9).toString());
            key.append("");
            key.append(vals.get(10).toString());
            key.append("");
            String f11 = vals.get(11).toString();
            int i11 = f11.indexOf('&');
            key.append(f11.substring(0, i11));
            key.append("");
            key.append(vals.get(12).toString());
            key.append("");
            key.append(vals.get(13).toString());
            key.append("");
            key.append(vals.get(14).toString());
            key.append("");
            String f15 = vals.get(15).toString();
            int i15 = f15.indexOf('&');
            if (i15 > -1)
            	key.append(f15.substring(0, i15));
            key.append(vals.get(15).toString());
            key.append("");
            key.append(vals.get(18).toString());
            key.append("");
            key.append(vals.get(19).toString());
            key.append("");
            String f20 = vals.get(20).toString();
            int i20 = f20.indexOf('&');
            key.append(f20.substring(0, i20));
            key.append("");
            key.append(vals.get(21).toString());
            key.append("");
            key.append(vals.get(22).toString());
            key.append("");
            key.append(vals.get(23).toString());
            key.append("");
            String f24 = vals.get(24).toString();
            int i24 = f24.indexOf('&');
            if (i24 > -1)
            	key.append(f24.substring(0, i24));
            key.append(vals.get(24).toString());
            
            StringBuffer sb = new StringBuffer();
            sb.append(f2.substring(i2 + 1));
            sb.append("");
            sb.append(f11.substring(i11 + 1));
            sb.append("");
            sb.append(f20.substring(i20 + 1));
            sb.append("");
            if (i6 > -1)
            	sb.append(f6.substring(i6 + 1));
            sb.append("");
            if (i15 > -1)
            	sb.append(f15.substring(i15 + 1));
            sb.append("");
            if (i24 > -1)
            	sb.append(f24.substring(i24 + 1));
            oc.collect(new Text(key.toString()), new Text(sb.toString()));
        }
    }

    public static class Combiner extends MapReduceBase
        implements Reducer<Text, Text, Text, Text> {

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
		
		public void reduce(
                Text key,
                Iterator<Text> iter, 
                OutputCollector<Text, Text> oc,
                Reporter reporter) throws IOException {
			int erCnt = 0;
            EncryptedNumber tsSum = zero, tsSum1 = zero, tsSum2 = zero,
            		erSum = zero, erSum1 = zero, erSum2 = zero;
            while (iter.hasNext()) {
                List<Text> vals = Library.splitLine(iter.next(), '');
                EncryptedNumber v = Util.getAHCipher(vals.get(0).toString(), context);
                tsSum = context.add(tsSum, v);
                v = Util.getAHCipher(vals.get(1).toString(), context);
                tsSum1 = context.add(tsSum1, v);
                v = Util.getAHCipher(vals.get(2).toString(), context);
                tsSum2 = context.add(tsSum2, v);
                String vs = vals.get(3).toString();
                if (!vs.isEmpty()) {
                	v = Util.getAHCipher(vs, context);
                    erSum = context.add(erSum, v);
                }
                vs = vals.get(4).toString();
                if (!vs.isEmpty()) {
                	v = Util.getAHCipher(vs, context);
                    erSum1 = context.add(erSum1, v);
                }
                vs = vals.get(5).toString();
                if (!vs.isEmpty()) {
                	v = Util.getAHCipher(vs, context);
                    erSum2 = context.add(erSum2, v);
                }
                erCnt++;
            }
            StringBuffer sb = new StringBuffer();
            sb.append(Util.getAHString(tsSum));
            sb.append("");
            sb.append(Util.getAHString(tsSum1));
            sb.append("");
            sb.append(Util.getAHString(tsSum2));
            sb.append("");
            sb.append(Util.getAHString(erSum));
            sb.append("");
            sb.append(Util.getAHString(erSum1));
            sb.append("");
            sb.append(Util.getAHString(erSum2));
            sb.append("");
            sb.append((new Integer(erCnt)).toString());
            oc.collect(key, new Text(sb.toString()));
            reporter.setStatus("OK");
        }
    }
    public static class Group extends MapReduceBase
        implements Reducer<Text, Text, Text, Text> {

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
		
		public void reduce(
                Text key,
                Iterator<Text> iter, 
                OutputCollector<Text, Text> oc,
                Reporter reporter) throws IOException {
        	int erCnt = 0;
            EncryptedNumber tsSum = zero, tsSum1 = zero, tsSum2 = zero,
            		erSum = zero, erSum1 = zero, erSum2 = zero;
            while (iter.hasNext()) {
                List<Text> vals = Library.splitLine(iter.next(), '');
                EncryptedNumber v = Util.getAHCipher(vals.get(0).toString(), context);
                tsSum = context.add(tsSum, v);
                v = Util.getAHCipher(vals.get(1).toString(), context);
                tsSum1 = context.add(tsSum1, v);
                v = Util.getAHCipher(vals.get(2).toString(), context);
                tsSum2 = context.add(tsSum2, v);
                String vs = vals.get(3).toString();
                if (!vs.isEmpty()) {
                	v = Util.getAHCipher(vs, context);
                    erSum = context.add(erSum, v);
                }
                vs = vals.get(4).toString();
                if (!vs.isEmpty()) {
                	v = Util.getAHCipher(vs, context);
                    erSum1 = context.add(erSum1, v);
                }
                vs = vals.get(5).toString();
                if (!vs.isEmpty()) {
                	v = Util.getAHCipher(vs, context);
                    erSum2 = context.add(erSum2, v);
                }
                erCnt++;
            }
            EncryptedNumber erAvg = erSum.divide(erCnt), erAvg1 = erSum1.divide(erCnt), erAvg2 = erSum2.divide(erCnt);
            StringBuffer sb = new StringBuffer();
            sb.append(Util.getAHString(tsSum));
            sb.append("\t");
            sb.append(Util.getAHString(tsSum1));
            sb.append("\t");
            sb.append(Util.getAHString(tsSum2));
            sb.append("\t");
            sb.append(Util.getAHString(erAvg));
            sb.append("\t");
            sb.append(Util.getAHString(erAvg1));
            sb.append("\t");
            sb.append(Util.getAHString(erAvg2));
            oc.collect(null, new Text(sb.toString()));
            reporter.setStatus("OK");
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length!=4) {
            System.out.println("Parameters: inputDir outputDir parallel pubKey");
            System.exit(1);
        }
        String inputDir = args[0];
        String outputDir = args[1];
        String parallel = args[2];
        JobConf lp = new JobConf(L17.class);
        lp.setJobName("L17 Wide group by");
        lp.set("pubKey", args[3]);
        lp.setInputFormat(TextInputFormat.class);
        lp.setOutputKeyClass(Text.class);
        lp.setOutputValueClass(Text.class);
        lp.setMapperClass(ReadPageViews.class);
        lp.setCombinerClass(Combiner.class);
        lp.setReducerClass(Group.class);
        Properties props = System.getProperties();
        for (Map.Entry<Object,Object> entry : props.entrySet()) {
            lp.set((String)entry.getKey(), (String)entry.getValue());
        }
        FileInputFormat.addInputPath(lp, new Path(inputDir + "/widegroupbydataCipher"));
        FileOutputFormat.setOutputPath(lp, new Path(outputDir + "/L17out"));
        lp.setNumReduceTasks(Integer.parseInt(parallel));
        Job group = new Job(lp);

        JobControl jc = new JobControl("L17 group by");
        jc.addJob(group);

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
