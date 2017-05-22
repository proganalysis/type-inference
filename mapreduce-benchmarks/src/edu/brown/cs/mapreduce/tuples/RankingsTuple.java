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
public class RankingsTuple extends AbstractTuple {

	/**
	 * 
	 */
	public RankingsTuple() {
		super(BenchmarkBase.RANKINGS_TYPES);
	}
	
	/**
	 * 
	 */
	public RankingsTuple(Writable[] values) {
		super(values, BenchmarkBase.RANKINGS_TYPES);
	}

}