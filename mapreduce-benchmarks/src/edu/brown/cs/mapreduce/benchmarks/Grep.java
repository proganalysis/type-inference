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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import edu.brown.cs.mapreduce.BenchmarkBase;

/**
 * @author pavlo
 *
 */

public class Grep extends Configured implements Tool {
	public static final int BASE_KEY_OFFSET = 10;
	public static final int SEQ_KEY_OFFSET = 0;
	
   
   public static class RegexMapper<K> extends MapReduceBase implements Mapper<K, Text, Text, Text> {
	  private String pattern;
      private Pattern regex_pattern;
      private int group;
      private Text dummy_marker = new Text("1");
      
      private boolean USE_SUBSTRING = false;
      private boolean USE_INDEXOF = false;
      private boolean USE_TEXTFIND = true; // NOTE: This is what was used in the SIGMOD/CACM papers
      private boolean USE_SIGMOD_VERSION = false; // NOTE: This was from Nov'08 testing and not in the SIGMOD paper. You want USE_TEXTFIND
      private boolean USE_SEQUENCEFILE = false;
      
      private int key_offset;
         
      public void configure(JobConf job) {
         super.configure(job);
         this.pattern = job.get(BenchmarkBase.PROPERTY_GREP_PATTERN);
         this.regex_pattern = Pattern.compile(this.pattern);
         this.group = job.getInt(BenchmarkBase.PROPERTY_GREP_MATCH_GROUP, 0);
         
         String property = job.get(BenchmarkBase.PROPERTY_GREP_SUBSTRING);
         if (property != null) {
            this.USE_SUBSTRING = Boolean.parseBoolean(property);
         }
         
         property = job.get(BenchmarkBase.PROPERTY_GREP_INDEXOF);
         if (property != null) {
            this.USE_INDEXOF = Boolean.parseBoolean(property);
         }
         
         property = job.get(BenchmarkBase.PROPERTY_GREP_TEXTFIND);
         if (property != null) {
            this.USE_TEXTFIND = Boolean.parseBoolean(property);
         }
         
         property = job.get(BenchmarkBase.PROPERTY_GREP_SIGMOD_VERSION);
         if (property != null) {
            this.USE_SIGMOD_VERSION = Boolean.parseBoolean(property);
         }
         
         property = job.get(BenchmarkBase.PROPERTY_SEQUENCEFILE);
         if (property != null) {
            this.USE_SEQUENCEFILE = Boolean.parseBoolean(property);
            this.key_offset = SEQ_KEY_OFFSET;
         } else {
        	 this.key_offset = BASE_KEY_OFFSET;
         }
         
         if (job.get(BenchmarkBase.PROPERTY_DEBUG) != null) {
	         System.out.println("PATTERN:          " + this.pattern);
	         System.out.println("MATCH_GROUP:      " + this.group);
	         System.out.println("USE_SEQUENCEFILE: " + this.USE_SEQUENCEFILE);
	         System.out.println("USE_SUBSTRING:    " + this.USE_SUBSTRING);
	         System.out.println("USE_TEXTFIND:     " + this.USE_TEXTFIND);
	         System.out.println("USE_INDEXOF:      " + this.USE_INDEXOF);
	         System.out.println("USE_SIGMOD_VER:   " + this.USE_SIGMOD_VERSION);
         }
      }
   
      public void map(K key, Text value,
                        OutputCollector<Text, Text> output,
                        Reporter reporter) throws IOException {
    	 String val_text = null;
    	 
    	 //
    	 // Search using Text.find()
    	 //
    	 if (USE_TEXTFIND) {
    		 if (value.find(this.pattern, this.key_offset) != -1) {
    			 if (this.USE_SEQUENCEFILE) {
	    			 output.collect((Text)key, value);
    			 } else {
    				 val_text = value.toString();
    				 output.collect(new Text(val_text.substring(0, BASE_KEY_OFFSET)),
			 						new Text(val_text.substring(BASE_KEY_OFFSET)));
    			 }
    			 //output.collect(value, dummy_marker);
    		 }
    	 //
    	 // Use simple String.indexOf
    	 //
    	 } else if (USE_INDEXOF) {
    		 val_text = value.toString();
    		 if (val_text.indexOf(this.pattern, this.key_offset) != -1) {
    			 if (this.USE_SEQUENCEFILE) {
    				 // TODO
    			 } else {
    				 output.collect(new Text(val_text.substring(0, BASE_KEY_OFFSET)),
    						 		new Text(val_text.substring(BASE_KEY_OFFSET)));	 
    			 }
    			 
    		 }
		 //
		 // SIGMOD 2009 Legacy Version (this is not the real benchmark)
		 //
    	 } else if (USE_SIGMOD_VERSION) {
    		 String text = (this.USE_SUBSTRING ? value.toString().substring(10, value.getLength()) : value.toString());
             Matcher matcher = regex_pattern.matcher(text);
             while (matcher.find()) {
                output.collect(new Text(group < 0 ? text : matcher.group(group)), this.dummy_marker);
             }
		 //
		 // Use Java's Regex Pattern
		 //
    	 } else {
	    	 if (USE_SUBSTRING) {
	    		 val_text = value.toString().substring(BASE_KEY_OFFSET);
	    	 } else {
	    		 val_text = value.toString();
	    	 }
	         Matcher matcher = regex_pattern.matcher(val_text);
	         if (matcher.find()) {
	        	 output.collect(new Text(value.toString().substring(0, BASE_KEY_OFFSET)),
	        			 		new Text(val_text));
	            //output.collect(new Text(group < 0 ? text : matcher.group(group)), new LongWritable(1));
	         }
    	 }
      }
   }
   
   /* (non-Javadoc)
    * @see org.apache.hadoop.util.Tool#run(java.lang.String[])
    */
   public int run(String[] args) throws Exception {
      BenchmarkBase base = new BenchmarkBase(this.getConf(), this.getClass(), args);
      
      // -------------------------------------------
      // Search
      // -------------------------------------------
      JobConf p1_job = base.getJobConf();
      //
      // We have to grab all of the dirs in the directory that was passed in
      //
//      List<Path> inputs = base.getInputPaths();
//      if (false && inputs.size() == 1) {
//         Path input_path = inputs.get(0);
//         FileSystem fs = null;
//         try {
//            fs = FileSystem.get(this.getConf());
//            if (fs.getFileStatus(input_path).isDir()) {
//               //p1_job.set("mapred.input.dir", "");
//               FileStatus paths[] = fs.listStatus(input_path);
//               for (FileStatus p : paths) {
//                  FileInputFormat.addInputPath(p1_job, p.getPath());
//               }
//            }
//         } catch (Exception ex) {
//            ex.printStackTrace();
//            System.exit(-1);
//         }
//      }      
      if (base.getSequenceFile()) p1_job.setInputFormat(SequenceFileInputFormat.class);
      p1_job.setMapperClass(Grep.RegexMapper.class);
      //p1_job.setCombinerClass(LongSumReducer.class);
      //p1_job.setReducerClass(LongSumReducer.class);

      //p1_job.setOutputFormat(SequenceFileOutputFormat.class);
      p1_job.setOutputKeyClass(Text.class);
      p1_job.setOutputValueClass(Text.class);
           
      base.runJob(p1_job);
      if (base.getCombine()) base.runCombine();
      
      return 0;
    }
}
