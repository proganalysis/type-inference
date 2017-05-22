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
package edu.brown.cs.mapreduce;


import java.util.*;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.util.*;

/**
 * @author pavlo
 *
 */
public class BenchmarkBase {
   //
   // Identifiers for the type of record
   //
   public static final Integer USERVISITS_KEY = 1;
   public static final IntWritable USERVISITS_KEY_WRITABLE = new IntWritable(USERVISITS_KEY);
   public static final Integer RANKINGS_KEY = 2;
   public static final IntWritable RANKINGS_KEY_WRITABLE = new IntWritable(RANKINGS_KEY);
   
   //
   // The fields for each record type
   //
   public static final String USERVISITS_FIELDS[] = { "sourceIP",
                                                      "destinationURL",
                                                      "visitDate",
                                                      "adRevenue",
                                                      "userAgent",
                                                      "countryCode",
                                                      "languageCode",
                                                      "searchKeyword",
                                                      "avgTimeOnSite"
   };
   public static final Class<?> USERVISITS_TYPES[] = { Text.class,
													   Text.class,
 													   Text.class,
 													   DoubleWritable.class,
 													   Text.class,
 													   Text.class,
 													   Text.class,
 													   Text.class,
 													   FloatWritable.class,
   };
   
   public static final String RANKINGS_FIELDS[] = { "pageURL",
                                                    "pageRank",
                                                    "avgDuration"
   };
   public static final Class<?> RANKINGS_TYPES[] = { Text.class,
	   											  IntWritable.class,
	   											  IntWritable.class,
   };
   
   //
   // Key/Value Separator
   //
   public static final String KEYVALUE_DELIMITER = "\t";
   public static final String VALUE_DELIMITER = "|";
   
   //
   // Special Properties
   //
   public static final String PROPERTY_MIN_PAGE_RANK 	= "mapreduce.minpagerank";
   public static final String PROPERTY_START_DATE 		= "mapreduce.startdate";
   public static final String PROPERTY_STOP_DATE 		= "mapreduce.stopdate";
   public static final String PROPERTY_SEQUENCEFILE 	= "mapreduce.sequencefile";
   public static final String PROPERTY_TUPLEDATA 		= "mapreduce.tupledata";
   public static final String PROPERTY_DEBUG 			= "mapreduce.debug";
   
   //
   // Benchmarks2 Properties
   //
   public static final String PROPERTY_BENCHMARKS2_SUBSTRING      = "mapreduce.benchmarks2.substring";
   
   //
   // Grep Benchmark Properties
   //
   public static final String PROPERTY_GREP_PATTERN        = "mapreduce.grep.pattern";
   public static final String PROPERTY_GREP_MATCH_GROUP    = "mapreduce.grep.match_group";
   public static final String PROPERTY_GREP_SUBSTRING      = "mapreduce.grep.substring";
   public static final String PROPERTY_GREP_INDEXOF        = "mapreduce.grep.indexof";
   public static final String PROPERTY_GREP_TEXTFIND 	   = "mapreduce.grep.textfind";
   public static final String PROPERTY_GREP_SIGMOD_VERSION = "mapreduce.grep.sigmod";
   
   protected final Configuration conf;
   protected final Class<?> benchmarkClass;
   protected final String args[];
   
   protected List<Path> input_paths;
   protected Path output_path;
   
   protected String job_name = null;
   protected Integer num_of_maps = -1;
   protected Integer num_of_reduces = -1;
   protected Boolean combine = false;
   protected Boolean compress = false;
   protected Boolean sequence_file = false;
   protected Boolean tuple_data = false;
   protected Boolean debug = false;
   protected JobConf last_job = null;
   
   protected Boolean load_directories = false;
   
   protected Hashtable<String, String> options = new Hashtable<String, String>();
   
   /**
    * 
    */
   public BenchmarkBase(Configuration conf, Class<?> benchmarkClass, String args[]) {
         this.conf = conf;
         this.benchmarkClass = benchmarkClass;
         this.args = args;
   }
   
   @SuppressWarnings("deprecation")
public JobConf getJobConf() {
      JobConf jobConf = new JobConf(this.conf, this.benchmarkClass);
      //
      // Options
      //
      List<String> otherArgs = new ArrayList<String>();
      for (int i = 0; i < args.length; i++) {
         try {
            //
            // Print property and exit
            //
            if ("-property".equals(args[i])) {
               String prop = jobConf.get(args[i + 1]);
               System.out.println(prop);
               System.exit(0);
            //
            // # of Maps
            //
            } else if ("-m".equals(args[i])) {
               this.num_of_maps = Integer.parseInt(args[++i]);
            //
            // # of Reduces
            //
            } else if ("-r".equals(args[i])) {
               this.num_of_reduces = Integer.parseInt(args[++i]);
           //
           // Enable debug
           //
           } else if ("-debug".equals(args[i])) {
              this.debug = true;
            //
            // Enable single output file for results
            //
            } else if ("-combine".equals(args[i])) {
               this.combine = true;
            //
            // Tell jobs to compress their intermediate output files
            //
            } else if ("-compress".equals(args[i])) {
               this.compress = true;
           //
           // We're using TupleWritable (which has to be in a SequenceFile)
           //
           } else if ("-tuple".equals(args[i])) {
              this.tuple_data = true;
              this.sequence_file = true;
            //
            // Use SequenceFiles for initial input
            //
            } else if ("-sequence".equals(args[i])) {
               this.sequence_file = true;
            //
            // Recursively load directories
            //
            } else if ("-recursive-dirs".equals(args[i])) {
               this.load_directories = true;
            //
            // Job Basename
            //
            } else if ("-basename".equals(args[i])) {
               this.job_name = args[++i];
            //
            // Misc. Properties
            //
            } else if ("-D".equals(args[i].substring(0, 2))) {
               String arg = args[i].substring(2);
               int pos = arg.indexOf('=');
               if (pos == -1) {
                  System.err.println("ERROR: Invalid properties option '" + arg + "'");
                  System.exit(1);
               }
               this.options.put(arg.substring(0, pos), arg.substring(pos + 1));
            } else {
               otherArgs.add(args[i]);
            }
         } catch (NumberFormatException except) {
            System.err.println("ERROR: Integer expected instead of " + args[i]);
            System.exit(1);
         } catch (ArrayIndexOutOfBoundsException except) {
            System.err.println("ERROR: Required parameter missing from " + args[i-1]);
            System.exit(1);
         }
      } // FOR
      //
      // Make sure there are exactly 2 parameters left.
      //
      if (otherArgs.size() < 2) {
         System.err.println("ERROR: Wrong number of parameters: " + otherArgs.size());
         System.exit(1);
      }
      
      //
      // Set these flags so the jobs know about them
      //
      if (this.getSequenceFile()) 	this.options.put(PROPERTY_SEQUENCEFILE, "true");
      if (this.getTupleData()) 		this.options.put(PROPERTY_TUPLEDATA, "true");
      if (this.getDebug()) 			this.options.put(PROPERTY_DEBUG, "true");
      
      FileSystem fs = null;
      try {
         fs = FileSystem.get(conf);
      } catch (Exception ex) {
         ex.printStackTrace();
         System.exit(-1);
      }
      
      //
      // Input Paths
      //
      int cnt = otherArgs.size() - 1;
      this.input_paths = new ArrayList<Path>();
      for (int ctr = 0; ctr < cnt; ctr++) {
         Path new_path = new Path(otherArgs.get(ctr));
         try {
            if (this.load_directories && fs.getFileStatus(new_path).isDir()) {
               //int limit = 10;
               FileStatus paths[] = fs.listStatus(new_path);
               for (FileStatus p : paths) {
                  this.input_paths.add(p.getPath());
                  FileInputFormat.addInputPath(jobConf, p.getPath());
                  //if (limit-- <= 0) break;
               } // FOR
            } else {
               this.input_paths.add(new_path);
               FileInputFormat.addInputPath(jobConf, new_path);
            }
         } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
         }
      } // FOR
      if (this.input_paths.isEmpty()) {
         System.err.println("ERROR: No input paths were defined for '" + this.benchmarkClass.getSimpleName() + "'");
         System.exit(-1);
      }
      
      //
      // Output Paths
      //
      this.output_path = new Path(otherArgs.get(otherArgs.size() - 1));
      FileOutputFormat.setOutputPath(jobConf, this.output_path);
      
      jobConf.setJobName(this.job_name != null ? this.job_name : this.benchmarkClass.getSimpleName());
      if (this.num_of_maps >= 0) jobConf.setNumMapTasks(this.num_of_maps);
      if (this.num_of_reduces >= 0) jobConf.setNumReduceTasks(this.num_of_reduces);
      
      //
      // Set all properties
      //
      for (String key : this.options.keySet()) {
         jobConf.set(key, this.options.get(key));
      }
      
      return (jobConf);
   }

   public void runJob(JobConf _conf) throws Exception {
      String ret = "BenchmarkBase(" + _conf.getJobName() + ")\n" +
                   "\tInput Path:  {";
      Path inputs[] = FileInputFormat.getInputPaths(_conf);
      for (int ctr = 0; ctr < inputs.length; ctr++) {
         if (ctr > 0) ret += ", ";
         ret += inputs[ctr].toString();
      }
      ret += "}\n";
      
      ret += "\tOutput Path: " + FileOutputFormat.getOutputPath(_conf) + "\n" +
             "\tMap Jobs:    " + _conf.getNumMapTasks() + "\n" +
             "\tReduce Jobs: " + _conf.getNumReduceTasks() + "\n" +
             "\tProperties:  " + this.options;
      System.out.println(ret);
      
      Date startTime = new Date();
      System.out.println("Job started: " + startTime);
      JobClient.runJob(_conf);
      Date end_time = new Date();
      System.out.println("Job ended: " + end_time);
      System.out.println("The job took " + (end_time.getTime() - startTime.getTime()) / (float)1000.0 + " seconds.");
      this.last_job = _conf;
      return;
   }
   
   public void runCombine() throws Exception {
      if (this.last_job == null) {
         throw new NullPointerException("ERROR: Last job is Null");
      }
      JobConf job = new JobConf(this.conf, this.benchmarkClass);
      job.setJobName((this.job_name != null ? this.job_name : this.benchmarkClass.getSimpleName()) + ".combine");
      job.setMapperClass(IdentityMapper.class);
      job.setNumMapTasks(0);
      job.setReducerClass(IdentityReducer.class);
      job.setNumReduceTasks(1); // this is needed to get a single output file

      // Input
      FileInputFormat.setInputPaths(job, FileOutputFormat.getOutputPath(this.last_job));
      job.setInputFormat(KeyValueTextInputFormat.class);
      
      // Output
      FileOutputFormat.setOutputPath(job, new Path(FileOutputFormat.getOutputPath(this.last_job).toString() + "/combine"));
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(Text.class);
      
      JobConf real_last_job = this.last_job;
      this.runJob(job);
      this.last_job = real_last_job;
      return;
   }
   
   public void setInputPaths(Path paths[]) {
      this.input_paths.clear();
      for (Path p : paths) {
         System.err.println("\tADD: " + p);
         this.input_paths.add(p);
      }
   }
   public List<Path> getInputPaths() {
      return (this.input_paths);
   }
   public Path getOutputPath() {
      return (this.output_path);
   }
   public Hashtable<String, String> getOptions() {
      return (this.options);
   }
   public Boolean getCombine() {
      return (this.combine);
   }
   public Boolean getCompress() {
      return (this.compress);
   }
   public Boolean getSequenceFile() {
      return (this.sequence_file);
   }
   public Boolean getTupleData() {
	   return (this.tuple_data);
   }
   public Boolean getDebug() {
	   return (this.debug);
   }

   
   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      String ret = "BenchmarkBase(" + this.benchmarkClass.getSimpleName() + ")\n" +
                   "\tInput Path:  " + this.input_paths + "\n" +
                   "\tOutput Path: " + this.output_path + "\n" +
                   "\tMap Jobs:    " + this.num_of_maps + "\n" +
                   "\tReduce Jobs: " + this.num_of_reduces + "\n" +
                   "\tProperties:  " + this.options;
      /*
      boolean first = true; 
      for (String key : this.options.keySet()) {
         if (!first) ret += ", ";
         ret += "[" + key + "->" + this.options.get(key) + "]";
         first = false;
      } // FOR
      */
      return (ret);
   }
   
   /**
    * 
    * @param args
    */
   @SuppressWarnings("unchecked")
   public static void main(String[] _args) {
      List<String> args = new ArrayList<String>();
      for (String arg : _args) {
         args.add(arg);
      } // FOR
      try {
         Class<? extends Configured> classDefinition = (Class<? extends Configured>)Class.forName(BenchmarkBase.class.getPackage().getName() + ".benchmarks." + args.get(0));
         Tool object = (Tool)classDefinition.newInstance();
         args.remove(0);
         int res = ToolRunner.run(new Configuration(), object, (String[])args.toArray(new String[0]));
         System.exit(res);
      } catch (InstantiationException e) {
          System.out.println(e);
      } catch (IllegalAccessException e) {
          System.out.println(e);
      } catch (ClassNotFoundException e) {
          System.out.println(e);
      } catch (Exception ex) {
         ex.printStackTrace();
         System.exit(-1);
      }
   }

}
