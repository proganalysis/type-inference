/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase3;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
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
		
		while (values.hasNext()) {
			TupleWritable value = values.next();
			//
			// <key> -> (<total_adRevenue>, <average_pageRank>)
			//
			if (value.size() == 2) {
				Double total_adRevenue = ((DoubleWritable)value.get(0)).get();
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
