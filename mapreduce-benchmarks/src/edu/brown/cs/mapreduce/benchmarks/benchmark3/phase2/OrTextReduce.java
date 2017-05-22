/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase2;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import edu.brown.cs.mapreduce.BenchmarkBase;

/**
 * @author pavlo
 *
 */
public class OrTextReduce extends BaseReduce implements Reducer<Text, Text, Text, Text> {
	public void reduce(Text key,
			   Iterator<Text> values,
			   OutputCollector<Text, Text> output,
			   Reporter reporter) throws IOException {
		if (this.output_text == null) this.output_text = output;
		
		Double total_adRevenue = 0.0d;
		Long counter = 0l;
		Long total_pageRank = 0l;
		while (values.hasNext()) {
			Text value = values.next();
			String fields[] = pattern.split(value.toString());
			//
			// <sourceIP> -> (<pageURL>, <pageRank>, <adRevenue>)
			//
			if (fields.length == 3) {
				total_adRevenue += Double.parseDouble(fields[2]);
				total_pageRank += Long.parseLong(fields[1]);
				counter++;
			//
			// Bad mojo!
			//
			} else {
				System.err.println("ERROR: Unexpected value '" + value + "' for key '" + key + "' in Phase2Reduce");
			}
		} // WHILE
		
		if (this.max_total_adRevenue == null || total_adRevenue > this.max_total_adRevenue) {
			this.max_total_adRevenue = total_adRevenue;
			Long average_pageRank = total_pageRank / (long)counter;
			this.max_val = new Text(total_adRevenue.toString() + BenchmarkBase.VALUE_DELIMITER + average_pageRank.toString());
			this.max_key = key;
		}
	}
}
