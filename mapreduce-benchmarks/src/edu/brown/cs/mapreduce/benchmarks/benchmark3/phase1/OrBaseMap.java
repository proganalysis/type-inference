/**
 * 
 */
package edu.brown.cs.mapreduce.benchmarks.benchmark3.phase1;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.hadoop.mapred.JobConf;
import edu.brown.cs.mapreduce.BenchmarkBase;
import edu.brown.cs.mapreduce.benchmarks.benchmark3.Benchmark3Base;

/**
 * @author pavlo
 *
 */
public abstract class OrBaseMap extends Benchmark3Base {
	public final static DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
	protected final static String props[] = { BenchmarkBase.PROPERTY_START_DATE, BenchmarkBase.PROPERTY_STOP_DATE };
	protected final static Integer START_DATE = 0;
	protected final static Integer STOP_DATE = 1;
	protected Date dates[] = { new Date(), new Date() };
		   
	public void configure(JobConf job) {
		super.configure(job);
		//
		// Get the date properties we need
		//
		Calendar cal = Calendar.getInstance();
		for (int ctr = 0; ctr < BaseMap.props.length; ctr++) {
			String prop_value = job.get(BaseMap.props[ctr]);
			if (prop_value == null) {
				System.err.println("ERROR: The property '" + BaseMap.props[ctr] + "' was not set!");
				System.exit(1);
			}
			try {
				cal.clear();
				this.dates[ctr] = BaseMap.dateParser.parse(prop_value);
				//System.err.println(BaseMapMap.props[ctr] + ": " + prop_value + " -> " + this.dates[ctr]);
			} catch (ParseException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		} // FOR
	}
}
