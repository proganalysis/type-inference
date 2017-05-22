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
import java.util.*;
import java.util.regex.*;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.LongSumReducer;
import org.apache.hadoop.util.*;
import edu.brown.cs.mapreduce.BenchmarkBase;

public class Benchmark4 extends Configured implements Tool {
   
   protected static final Pattern pattern = Pattern.compile("<a href=\"http://([\\w\\d]+\\.html)\">link</a>", Pattern.CASE_INSENSITIVE);
   
   public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable> {
      public void map(LongWritable key,
                      Text value,
                      OutputCollector<Text, LongWritable> output, 
                      Reporter reporter) throws IOException {
         String input = value.toString();
         Matcher m = pattern.matcher(input);
         
         HashMap<String, Integer> pageRank = new HashMap<String, Integer>();
         while(m.find()) {
            String url = input.substring(m.start(1), m.end(1));
            if (!pageRank.containsKey(url)) {
               pageRank.put(url, 1);
            } else {
               pageRank.put(url, pageRank.get(url) + 1);
            }
         } // WHILE
         
         for (String url : pageRank.keySet()) {
            try {
               output.collect(new Text(url), new LongWritable(pageRank.get(url)));
            } catch (NumberFormatException ex) {
               ex.printStackTrace();
            }
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
      
      job.setInputFormat(TextInputFormat.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(LongWritable.class);      
      job.setMapperClass(Benchmark4.Map.class);
      job.setCombinerClass(LongSumReducer.class);
      job.setReducerClass(LongSumReducer.class);   
      
      try {
         job.setCompressMapOutput(base.getCompress());
         base.runJob(job);
         
         if (base.getCombine()) base.runCombine();
      } catch (Exception ex) {
         ex.printStackTrace();
         System.exit(1);
      }
      return 0;
    }
}
