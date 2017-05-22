/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase3;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/**
 * @author pavlo
 *
 */
public class TextReduce extends BaseReduce implements Reducer<Text, Text, Text, Text> {
	public void reduce(Text key,
			   Iterator<Text> values,
			   OutputCollector<Text, Text> output,
			   Reporter reporter) throws IOException {
		if (this.output_text == null) this.output_text = output;
		
		while (values.hasNext()) {
			Text value = values.next();
			String fields[] = pattern.split(value.toString());
			//
			// <key> -> (<total_adRevenue>, <average_pageRank>)
			//
			if (fields.length == 2) {
				Double total_adRevenue  = Double.parseDouble(fields[0]);
				if (this.max_total_adRevenue == null || total_adRevenue > this.max_total_adRevenue) {
					this.max_total_adRevenue = total_adRevenue;
					this.max_key = key;
					this.max_val = value;
				}
			//
			// Bad mojo!
			//
			} else {
				System.err.println("ERROR: Unexpected value '" + value + "' for key '" + key + "' in Phase3Reduce");
			}
		} // WHILE
	}

}
