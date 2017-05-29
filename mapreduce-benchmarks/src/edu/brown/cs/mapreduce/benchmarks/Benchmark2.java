/***************************************************************************
*   Copyright (C) 2008 by Andy Pavlo, Brown University                    *
*   http://www.cs.brown.edu/~pavlo/                                       *
*                                                                         *
*   Permission is hereby granted, free of charge, to any person obtaining *
*   a copy of this software and associated documentation files (the       *
*   "Software"), to deal in the Software without restriction, including   *
*   without limitation the rights to use, copy, modify, merge, publish,   *
*   distribute, sublicense, and/or sell copies of the Software, and to    *
*   permit persons to whom the Software is furnished to do so, subject to *
*   the following conditions:                                             *
*                                                                         *
*   The above copyright notice and this permission notice shall be        *
*   included in all copies or substantial portions of the Software.       *
*                                                                         *
*   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,       *
*   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF    *
*   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.*
*   IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR     *
*   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, *
*   ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR *
*   OTHER DEALINGS IN THE SOFTWARE.                                       *
***************************************************************************/
package edu.brown.cs.mapreduce.benchmarks;

import java.io.*;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.join.TupleWritable;
import org.apache.hadoop.util.*;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPublicKey;

import edu.brown.cs.mapreduce.BenchmarkBase;
import encryption.Util;

public class Benchmark2 extends Configured implements Tool {

	public static class BaseMap extends MapReduceBase {
		protected boolean USE_SUBSTRING = false;
		protected boolean SHOWN_ERROR = false;

		public void configure(JobConf job) {
			super.configure(job);
			//
			// Get the page rank from the conf object
			//
			String property = job.get(BenchmarkBase.PROPERTY_BENCHMARKS2_SUBSTRING);
			if (property != null) {
				this.USE_SUBSTRING = Boolean.parseBoolean(property);
			}
			// this.USE_SUBSTRING = true; // HACK FOR NOW
			// System.out.println("USE_SUBSTRING: " + this.USE_SUBSTRING);
		}
	}

	public static class TupleWritableMap extends BaseMap implements Mapper<Text, TupleWritable, Text, DoubleWritable> {
		public void map(Text key, TupleWritable value, OutputCollector<Text, DoubleWritable> output, Reporter reporter)
				throws IOException {
			//
			// The *third* field should be our revenue
			//
			DoubleWritable adRevenue = (DoubleWritable) value.get(2);
			if (this.USE_SUBSTRING) {
				key = new Text(key.toString().substring(0, 7));
			}
			output.collect(key, adRevenue);
		}
	}

	public static class TextMap extends BaseMap implements Mapper<Text, Text, Text, Text> {
		private final Pattern pattern = Pattern.compile("\\" + BenchmarkBase.VALUE_DELIMITER);

		public void map(Text key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			//
			// Split the value using VALUE_DELIMITER into separate fields
			// The *third* field should be our revenue
			//
			String fields[] = pattern.split(value.toString());
			String keys = key.toString();
			int index = keys.indexOf('&');
			key = new Text(keys.substring(0, index));
			if (fields.length > 0) {
				try {
					// Double revenue = Double.parseDouble(fields[2]);
					if (this.USE_SUBSTRING) {
						// key = new Text(key.toString().substring(0, 7));
						key = new Text(keys.substring(index + 1));
					}
					output.collect(key, new Text(fields[2]));
				} catch (ArrayIndexOutOfBoundsException ex) {
					System.err.println("ERROR: Invalid record for key '" + key + "'");
					if (!this.SHOWN_ERROR) {
						for (int i = 0; i < fields.length; i++) {
							System.err.println("[" + i + "] " + fields[i]);
						} // FOR
						this.SHOWN_ERROR = true;
					}
				} catch (NumberFormatException ex) {
					System.err.println("ERROR: Invalid adRevenue field for key '" + key + "'");
					ex.printStackTrace();
					// System.exit(1);
				}
			} /*
				 * int size = value.size(); if (size > 0) { try { Integer
				 * revenue = Integer.parseInt(value.get(size - 1).toString());
				 * output.collect(key, new IntWritable(revenue)); } catch
				 * (NumberFormatException ex) { ex.printStackTrace();
				 * System.exit(1); } }
				 */
			return;
		}
	} // END CLASS

	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
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

		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
				throws IOException {
			//long sum = 0;
			EncryptedNumber sum = zero;
			while (values.hasNext()) {
				//sum += values.next().get();
				sum = context.add(sum, Util.getAHCipher(values.next().toString(), context));
			} // WHILE
			output.collect(key, new Text(Util.getAHString(sum)));
		}
	} // END CLASS

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.util.Tool#run(java.lang.String[])
	 */
	public int run(String[] args) throws Exception {
		BenchmarkBase base = new BenchmarkBase(this.getConf(), this.getClass(), args);
		JobConf job = base.getJobConf();

		job.setInputFormat(base.getSequenceFile() ? SequenceFileInputFormat.class : KeyValueTextInputFormat.class);
		// job.setInputFormat(KeyValueSetInputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		if (base.getTupleData()) {
			job.setMapperClass(Benchmark2.TupleWritableMap.class);
		} else {
			job.setMapperClass(Benchmark2.TextMap.class);
		}
		job.setCombinerClass(Benchmark2.Reduce.class);
		job.setReducerClass(Benchmark2.Reduce.class);
		// job.setNumReduceTasks(0);

		try {
			base.runJob(job);
			if (base.getCombine())
				base.runCombine();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		return 0;
	}
}
