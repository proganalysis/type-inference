/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.hadoop.mapred.JobConf;
import edu.brown.cs.mapreduce.BenchmarkBase;
import edu.brown.cs.mapreduce.benchmarks.benchmark3.Benchmark3Base;

/**
 * @author pavlo
 *
 */
public abstract class BaseMap extends Benchmark3Base {
	public final static DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
	protected final static String props[] = { BenchmarkBase.PROPERTY_START_DATE, BenchmarkBase.PROPERTY_STOP_DATE };
	protected final static Integer START_DATE = 0;
	protected final static Integer STOP_DATE = 1;
	protected long dates_sen[] = new long[2];
		   
	public void configure(JobConf job) {
		super.configure(job);
		//
		// Get the date properties we need
		//
		for (int ctr = 0; ctr < BaseMap.props.length; ctr++) {
			String prop_value = job.get(BaseMap.props[ctr]);
			if (prop_value == null) {
				System.err.println("ERROR: The property '" + BaseMap.props[ctr] + "' was not set!");
				System.exit(1);
			}
			this.dates_sen[ctr] = Long.parseLong(prop_value);
		} // FOR
	}
}
