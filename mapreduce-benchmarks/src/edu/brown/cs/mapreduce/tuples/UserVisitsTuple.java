/**
 * 
 */
package edu.brown.cs.mapreduce.tuples;

import org.apache.hadoop.io.Writable;
import edu.brown.cs.mapreduce.BenchmarkBase;

/**
 * @author pavlo
 *
 */
public class UserVisitsTuple extends AbstractTuple {

	/**
	 * 
	 */
	public UserVisitsTuple() {
		super(BenchmarkBase.USERVISITS_TYPES);
	}
	
	/**
	 * 
	 */
	public UserVisitsTuple(Writable[] values) {
		super(values, BenchmarkBase.USERVISITS_TYPES);
	}

}