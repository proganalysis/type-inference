/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase1;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.join.TupleWritable;

import edu.brown.cs.mapreduce.BenchmarkBase;

/**
 * @author pavlo
 *
 */
public class TupleWritableMap extends OrBaseMap implements Mapper<Text, TupleWritable, TupleWritable, TupleWritable> {
	public void map(Text key,
			TupleWritable value, 
			OutputCollector<TupleWritable, TupleWritable> output, 
			Reporter reporter) throws IOException {
		//
		// HACK:
		// We have to figure out what the input type is based on the number of fields
		//
		TupleWritable output_key = null;
		TupleWritable output_value = null;
		int num_of_fields = value.size();
		if (num_of_fields == (BenchmarkBase.RANKINGS_FIELDS.length - 1)) {
			Writable values[] = { key, BenchmarkBase.RANKINGS_KEY_WRITABLE };
			output_key = new TupleWritable(values);
			output_value = value;
		} else if (num_of_fields == (BenchmarkBase.USERVISITS_FIELDS.length - 1)) {
			try {
				//
				// Grab the visitDate field and check whether it is in the range we are looking for
				//
				Date date = BaseMap.dateParser.parse(value.get(1).toString());
				if (date.compareTo(this.dates[BaseMap.START_DATE]) >= 0 &&
					date.compareTo(this.dates[BaseMap.STOP_DATE]) <= 0) {
					Writable values[] = { value.get(0), BenchmarkBase.USERVISITS_KEY_WRITABLE };
					output_key = new TupleWritable(values);
					//
					// Since we are now going to use the URL as they composite key, we 
					// need to swap the source IP and URL from the values
					//
					values = new Writable[num_of_fields];
					for (int ctr = 0; ctr < num_of_fields; ctr++) {
						if (ctr == 0) {
							values[ctr] = key;
						} else {
							values[ctr] = value.get(ctr);
						}
					} // FOR
					output_value = new TupleWritable(values);
				} // IF
			} catch (ParseException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		} else {
			System.err.println("ERROR: The input value on key '" + key + "' to BaseMapMap has '" + num_of_fields + "' fields! Skipping...");
			for (int i = 0; i < num_of_fields; i++) {
				System.err.println("[" + i + "] " + value.get(i).toString());
			}
		}
		if (output_key != null) output.collect(output_key, output_value);
		return;
	}
} // END CLASS
