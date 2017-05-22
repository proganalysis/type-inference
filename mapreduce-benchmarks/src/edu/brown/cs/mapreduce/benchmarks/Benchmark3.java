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

import java.util.*;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.fs.*;
import edu.brown.cs.mapreduce.BenchmarkBase;

public class Benchmark3 extends Configured implements Tool {
   
   public static String getTypeString(int type) {
      if (type == 1) {
         return ("UserVisits");
      } else if (type == 2) {
         return ("Rankings");
      }
      return ("INVALID");
   }
   
   
   /* (non-Javadoc)
    * @see org.apache.hadoop.util.Tool#run(java.lang.String[])
    */
   public int run(String[] args) throws Exception {
      BenchmarkBase base = new BenchmarkBase(this.getConf(), this.getClass(), args);
      
      Date startTime = new Date();
      System.out.println("Job started: " + startTime);
      
      // -------------------------------------------
      // Phase #1
      // -------------------------------------------
      JobConf p1_job = base.getJobConf();
      p1_job.setJobName(p1_job.getJobName() + ".Phase1");
      Path p1_output = new Path(base.getOutputPath().toString() + "/phase1");
      FileOutputFormat.setOutputPath(p1_job, p1_output);
      
      //
      // Make sure we have our properties
      //
      String required[] = { BenchmarkBase.PROPERTY_START_DATE, BenchmarkBase.PROPERTY_STOP_DATE };
      for (String req : required) {
         if (!base.getOptions().containsKey(req)) {
            System.err.println("ERROR: The property '" + req + "' is not set");
            System.exit(1);
         }
      } // FOR
      
      p1_job.setInputFormat(base.getSequenceFile() ? SequenceFileInputFormat.class : KeyValueTextInputFormat.class);
      if (base.getSequenceFile()) p1_job.setOutputFormat(SequenceFileOutputFormat.class);
      p1_job.setOutputKeyClass(Text.class);
      p1_job.setOutputValueClass(Text.class);
      p1_job.setMapperClass(base.getTupleData() ? edu.brown.cs.mapreduce.benchmarks.benchmark3.phase1.TupleWritableMap.class : edu.brown.cs.mapreduce.benchmarks.benchmark3.phase1.TextMap.class);
      p1_job.setReducerClass(base.getTupleData() ? edu.brown.cs.mapreduce.benchmarks.benchmark3.phase1.TupleWritableReduce.class : edu.brown.cs.mapreduce.benchmarks.benchmark3.phase1.TextReduce.class);
      p1_job.setCompressMapOutput(base.getCompress());
      
      
      // -------------------------------------------
      // Phase #2
      // -------------------------------------------
      JobConf p2_job = base.getJobConf();
      p2_job.setJobName(p2_job.getJobName() + ".Phase2");
      p2_job.setInputFormat(base.getSequenceFile() ? SequenceFileInputFormat.class : KeyValueTextInputFormat.class);
      if (base.getSequenceFile()) p2_job.setOutputFormat(SequenceFileOutputFormat.class);
      p2_job.setOutputKeyClass(Text.class);
      p2_job.setOutputValueClass(Text.class);
      p2_job.setMapperClass(IdentityMapper.class);
      p2_job.setReducerClass(base.getTupleData() ? edu.brown.cs.mapreduce.benchmarks.benchmark3.phase2.TupleWritableReduce.class : edu.brown.cs.mapreduce.benchmarks.benchmark3.phase2.TextReduce.class);
      p2_job.setCompressMapOutput(base.getCompress());
      
      // -------------------------------------------
      // Phase #3
      // -------------------------------------------
      JobConf p3_job = base.getJobConf();
      p3_job.setJobName(p3_job.getJobName() + ".Phase3");
      p3_job.setNumReduceTasks(1);
      p3_job.setInputFormat(base.getSequenceFile() ? SequenceFileInputFormat.class : KeyValueTextInputFormat.class);
      p3_job.setOutputKeyClass(Text.class);
      p3_job.setOutputValueClass(Text.class);
      //p3_job.setMapperClass(Phase3Map.class);
      p3_job.setMapperClass(IdentityMapper.class);
      p3_job.setReducerClass(base.getTupleData() ? edu.brown.cs.mapreduce.benchmarks.benchmark3.phase3.TupleWritableReduce.class : edu.brown.cs.mapreduce.benchmarks.benchmark3.phase3.TextReduce.class);

      //
      // Execute #1
      //
      base.runJob(p1_job);
      
      //
      // Execute #2
      //
      Path p2_output = new Path(base.getOutputPath().toString() + "/phase2");
      FileOutputFormat.setOutputPath(p2_job, p2_output);
      FileInputFormat.setInputPaths(p2_job, p1_output);
      base.runJob(p2_job);

      //
      // Execute #3
      //
      Path p3_output = new Path(base.getOutputPath().toString() + "/phase3");
      FileOutputFormat.setOutputPath(p3_job, p3_output);
      FileInputFormat.setInputPaths(p3_job, p2_output);
      base.runJob(p3_job);
      
      // There does need to be a combine if (base.getCombine()) base.runCombine();

      return 0;
    }
}
