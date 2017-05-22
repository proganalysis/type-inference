/***************************************************************************
*   Copyright (C) 2009 by Andy Pavlo, Brown University                    *
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

import java.io.IOException;
import java.util.regex.Pattern;

import edu.brown.cs.mapreduce.*;
import edu.brown.cs.mapreduce.tuples.RankingsTuple;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class Benchmark1 extends Configured implements Tool {

	public static class BaseMap extends MapReduceBase {
		protected Integer MIN_PAGE_RANK = null;
		
		public void configure(JobConf job) {
	        super.configure(job);
	        //
	        // Get the page rank from the conf object
	        //
	        String property = job.get(BenchmarkBase.PROPERTY_MIN_PAGE_RANK);
	        if (property == null) {
	           System.err.println("ERROR: The property '" + BenchmarkBase.PROPERTY_MIN_PAGE_RANK + "' was not set!");
	           System.exit(1);
	        }
	        this.MIN_PAGE_RANK = Integer.parseInt(property);
	     }
	}
	
	public static class TupleMap extends BaseMap implements Mapper<Text, RankingsTuple, Text, IntWritable> {
	      public void map(Text key,
	    		  		  RankingsTuple value, 
	                      OutputCollector<Text, IntWritable> output, 
	                      Reporter reporter) throws IOException {
	         IntWritable pageRank = (IntWritable)value.get(0);
	         System.err.println(key + " => " + value.get(0) + " [" + value + "]");
             if (pageRank.get() > this.MIN_PAGE_RANK) {
	            output.collect(key, pageRank);
	         }
	         return;
	      }
	   } // END CLASS
	
   public static class TextMap extends BaseMap implements Mapper<Text, Text, Text, IntWritable> {
	  private final Pattern pattern = Pattern.compile("\\" + BenchmarkBase.VALUE_DELIMITER);
	   
      public void map(Text key,
                      Text value, 
                      OutputCollector<Text, IntWritable> output, 
                      Reporter reporter) throws IOException {
         String data[] = pattern.split(value.toString());
         Integer temp = Integer.valueOf(data[0]);
         if (temp > this.MIN_PAGE_RANK) {
            output.collect(key, new IntWritable(temp));
         }
         return;
      }
   } // END CLASS
   
   /* (non-Javadoc)
    * @see org.apache.hadoop.util.Tool#run(java.lang.String[])
    */
   public int run(String[] args) throws Exception {
      BenchmarkBase base = new BenchmarkBase(this.getConf(), this.getClass(), args);
      JobConf job = base.getJobConf();
      
      job.setInputFormat(base.getSequenceFile() ? SequenceFileInputFormat.class : KeyValueTextInputFormat.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(IntWritable.class);
      if (base.getTupleData()) {
    	  job.setMapperClass(Benchmark1.TupleMap.class);
      } else {
    	  job.setMapperClass(Benchmark1.TextMap.class);
      }
      //job.setReducerClass(IdentityReducer.class);
      job.setNumReduceTasks(0);
            
      try {
         base.runJob(job);
         if (base.getCombine()) base.runCombine();
      } catch (Exception ex) {
         ex.printStackTrace();
         System.exit(1);
      }
      return 0;
    }
}
