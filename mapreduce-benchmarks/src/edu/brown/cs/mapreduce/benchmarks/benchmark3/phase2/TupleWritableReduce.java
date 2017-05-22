/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase2;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.join.TupleWritable;

/**
 * @author pavlo
 *
 */
public class TupleWritableReduce extends BaseReduce implements Reducer<Text, TupleWritable, Text, TupleWritable> {
	public void reduce(Text key,
			   Iterator<TupleWritable> values,
			   OutputCollector<Text, TupleWritable> output,
			   Reporter reporter) throws IOException {
		if (this.output_tuple == null) this.output_tuple = output;
		
		Double total_adRevenue = 0.0d;
		Long counter = 0l;
		Long total_pageRank = 0l;
		while (values.hasNext()) {
			TupleWritable value = values.next();
			//
			// <sourceIP> -> (<pageURL>, <pageRank>, <adRevenue>)
			//
			if (value.size() == 3) {
				total_adRevenue += ((DoubleWritable)value.get(2)).get();
				total_pageRank += ((LongWritable)value.get(1)).get();
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
			Writable output_values[] = new Writable[2];
			output_values[0] = new DoubleWritable(total_adRevenue);
			output_values[1] = new LongWritable(average_pageRank);
			
			this.max_val = new TupleWritable(output_values);
			this.max_key = key;
		}
	}
}