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
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import edu.brown.cs.mapreduce.BenchmarkBase;

/**
 * @author pavlo
 *
 */

public class DummyJob extends Configured implements Tool {
   
   public static class Map<K> extends MapReduceBase implements Mapper<K, Text, Text, LongWritable> {
      
      protected final Date prog_start = new Date();
      protected Date map_start = null;
      
      public void map(K key, Text value,
                        OutputCollector<Text, LongWritable> output,
                        Reporter reporter) throws IOException {
         //
         // Do nothing!
         //
         if (map_start == null) map_start = new Date();
      }
      
      @Override
      public void close() throws IOException {
         Date stop = new Date();
         
         System.out.println("PROG START: " + this.prog_start.getTime());
         System.out.println("MAP START: " + (this.map_start != null ? this.map_start.getTime() : "null"));
         System.out.println("STOP: " + stop.getTime());
         System.out.println("PROG-MAP DIFF:  " + (map_start != null ? ((map_start.getTime() - prog_start.getTime()) / (float)1000.0) + " seconds." : "null"));
         System.out.println("PROG-STOP DIFF: " + (stop.getTime() - prog_start.getTime()) / (float)1000.0 + " seconds.");
         
         super.close();
      }
   }
   
   /* (non-Javadoc)
    * @see org.apache.hadoop.util.Tool#run(java.lang.String[])
    */
   public int run(String[] args) throws Exception {
      BenchmarkBase base = new BenchmarkBase(this.getConf(), this.getClass(), args);
      JobConf p1_job = base.getJobConf();
      if (base.getSequenceFile()) p1_job.setInputFormat(SequenceFileInputFormat.class);
      p1_job.setMapperClass(DummyJob.Map.class);
      p1_job.setOutputKeyClass(Text.class);
      p1_job.setOutputValueClass(LongWritable.class);
      base.runJob(p1_job);
      return 0;
    }
}