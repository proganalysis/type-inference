/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.join.TupleWritable;

import edu.brown.cs.mapreduce.BenchmarkBase;
import edu.brown.cs.mapreduce.benchmarks.benchmark3.Benchmark3Base;

/**
 * @author pavlo
 *
 */
public class TupleWritableReduce extends Benchmark3Base implements Reducer<TupleWritable, TupleWritable, Text, TupleWritable> {
	public void reduce(TupleWritable key,
			   Iterator<TupleWritable> values,
			   OutputCollector<Text, TupleWritable> output,
			   Reporter reporter) throws IOException {
		//
		// Get the URL key
		//
		TupleWritable ranking = null;
		List<TupleWritable> uservisits = new ArrayList<TupleWritable>();
		
		while (values.hasNext()) {
			TupleWritable value = values.next();
			int num_of_fields = value.size();
			//
			// Rankings
			//
			if (num_of_fields == (BenchmarkBase.RANKINGS_FIELDS.length - 1)) {
				//
				// We used to only have a single ranking per key, but since we 
				// may be using duplicated input files, we should just siliently ignore these 
				//
				if (ranking != null) {
					// System.err.println("ERROR: Found more than one ranking for key '" + key + "' [" + value + ", " + ranking + "]");
					// System.exit(1);
					continue;
				}
				ranking = value;
			//
			// UserVisits
		 //
			} else if (num_of_fields == (BenchmarkBase.USERVISITS_FIELDS.length - 1)) {
				//
				// Just add it to our list
				//
				uservisits.add(value);
			//
			// Bad mojo!
			//
			} else {
				System.err.println("ERROR: Unexpected value '" + value + "' for key '" + key + "' in Phase1Reduce");
			}
		} // WHILE
		
		if (ranking == null) {
			System.err.println("ERROR: We did not find a ranking record for key '" + key + "'");
			System.exit(1);
		} 
		
		//
		// Perform the join and push them out
		// We do not need to throw an error if there are no uservisit records
		// Our output record will look like:
		//
		//    <sourceIP> -> (<pageURL>, <pageRank>, <adRevenue>)
		//
		for (TupleWritable value : uservisits) {
			Writable output_values[] = new Writable[3];
			Text output_key = (Text)value.get(0);			// sourceIP
			output_values[0] = key;							// pageURL
			output_values[1] = ranking.get(0);				// pageRank
			output_values[2] = value.get(2);				// adRevenue
			output.collect(output_key, new TupleWritable(output_values));
		} // FOR
	}
} // END CLASS