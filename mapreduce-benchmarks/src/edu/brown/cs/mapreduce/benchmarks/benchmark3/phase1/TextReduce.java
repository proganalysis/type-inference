/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import edu.brown.cs.mapreduce.BenchmarkBase;
import edu.brown.cs.mapreduce.benchmarks.benchmark3.Benchmark3Base;

/**
 * @author pavlo
 *
 */
public class TextReduce extends Benchmark3Base implements Reducer<Text, Text, Text, Text> {
	public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		//
		// Get the URL key
		//
		// CompositeKey ckey = null;
		String[] ranking = null;
		List<String[]> uservisits = new ArrayList<String[]>();

		while (values.hasNext()) {
			Text value = values.next();
			String fields[] = pattern.split(value.toString());
			//
			// Rankings
			//
			if (fields.length == (BenchmarkBase.RANKINGS_FIELDS.length - 1)) {
				//
				// We used to only have a single ranking per key, but since we
				// may be using duplicated input files, we should just silently
				// ignore these
				//
				if (ranking != null) {
					// System.err.println("ERROR: Found more than one ranking
					// for key '" + key + "' [" + value + ", " + ranking + "]");
					// System.exit(1);
					continue;
				}
				ranking = fields;
				//
				// UserVisits
				//
			} else if (fields.length == (BenchmarkBase.USERVISITS_FIELDS.length - 1)) {
				//
				// Just add it to our list
				//
				uservisits.add(fields);
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
		// <sourceIP> -> (<pageURL>, <pageRank>, <adRevenue>)
		//
		if (this.DEBUG)
			System.out.println("# of Matching UserVisits: " + uservisits.size());
		for (int ctr = 0, cnt = uservisits.size(); ctr < cnt; ctr++) {
			String fields[] = uservisits.get(ctr);
			Text _key = new Text(fields[0]); // pageURL
			Text _val = new Text(key.toString() + BenchmarkBase.VALUE_DELIMITER + // pageURL
					ranking[0] + BenchmarkBase.VALUE_DELIMITER + // pageRank
					fields[2]); // adRevenue
			output.collect(_key, _val);
		} // FOR
	}
} // END CLASS