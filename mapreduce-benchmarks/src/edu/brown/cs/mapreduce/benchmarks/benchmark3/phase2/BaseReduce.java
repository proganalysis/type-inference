/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.join.TupleWritable;

import edu.brown.cs.mapreduce.benchmarks.benchmark3.Benchmark3Base;

/**
 * @author pavlo
 *
 */
public abstract class BaseReduce extends Benchmark3Base {
	protected OutputCollector<Text, Text> output_text = null;
	protected OutputCollector<Text, TupleWritable> output_tuple = null;
	protected Double max_total_adRevenue = null;
	protected Text max_key = null;
	protected Writable max_val = null;		
	
	@Override
	public void close() throws IOException {
		// 
		// Push out the max record: <sourceIP> -> (<max total adRevenue>, <average pageRank>)
		//
		if (this.max_total_adRevenue != null) {
			if (this.output_tuple != null)
				this.output_tuple.collect(this.max_key, (TupleWritable)this.max_val);
			else if (this.output_text != null)
				this.output_text.collect(this.max_key, (Text)this.max_val);
			else {
				System.err.println("ERROR: The output collector for Phase2 was not set!!");
				System.exit(1);
			}
		}
		super.close();
	}
}
