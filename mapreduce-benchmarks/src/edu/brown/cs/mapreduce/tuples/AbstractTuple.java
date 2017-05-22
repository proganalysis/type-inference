/**
 * 
 */
package edu.brown.cs.mapreduce.tuples;

import java.io.*;
import java.util.*;

import org.apache.hadoop.io.Writable;

/**
 * @author pavlo
 *
 */
public abstract class AbstractTuple implements Writable, Iterable<Writable> {

	protected Writable[] values;
	protected final Class<?>[] types;
	
	public AbstractTuple(Class<?>[] types) {
		this.types = types;
	}
	
	/**
	 * Initialize tuple with storage; unknown whether any of them contain
	 * &quot;written&quot; values.
	 */
	public AbstractTuple(Writable[] vals, Class<?>[] types) {
		this(types);
		this.values = vals;
	}

	/**
	 * Get ith Writable from Tuple.
	 */
	public Writable get(int i) {
		return values[i];
	}

	/**
	 * The number of children in this Tuple.
	 */
	public int size() {
		return values.length;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object other) {
		if (other instanceof AbstractTuple) {
			AbstractTuple that = (AbstractTuple)other;
			if (this.size() != that.size()) {
				return false;
			}
			for (int i = 0; i < values.length; ++i) {
				if (!values[i].equals(that.get(i))) {
					return false;
				}
			} // FOR
			return true;
		}
		return false;
	}

	public int hashCode() {
		return (int)values.hashCode();
	}

	/**
	 * Return an iterator over the elements in this tuple.
	 * Note that this doesn't flatten the tuple; one may receive tuples
	 * from this iterator.
	 */
	public Iterator<Writable> iterator() {
		final AbstractTuple t = this;
		return new Iterator<Writable>() {
			long i = values.length;
			long last = 0L;
			public boolean hasNext() {
				return 0L != i;
			}
			public Writable next() {
				last = Long.lowestOneBit(i);
				if (0 == last)
					throw new NoSuchElementException();
				i ^= last;
				// numberOfTrailingZeros rtn 64 if lsb set
				return t.get(Long.numberOfTrailingZeros(last) % 64);
			}
			public void remove() {
				// t.written ^= last;
			}
		};
	}

	/**
	 * Convert Tuple to String as in the following.
	 * <tt>[<child1>,<child2>,...,<childn>]</tt>
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("[");
		for (int i = 0; i < values.length; ++i) {
			buf.append(values[i].toString());
			buf.append(",");
		}
		if (values.length != 0)
			buf.setCharAt(buf.length() - 1, ']');
		else
			buf.append(']');
		return buf.toString();
	}

	/** Writes each Writable to <code>out</code>.
	 * TupleWritable format:
	 * {@code
	 *  <count><type1><type2>...<typen><obj1><obj2>...<objn>
	 * }
	 */
	public void write(DataOutput out) throws IOException {
		for (int i = 0; i < values.length; ++i)
			values[i].write(out);
	}

	/**
	 * {@inheritDoc}
	 */
	public void readFields(DataInput in) throws IOException {
		int card = this.types.length - 1;
		this.values = new Writable[card];
		try {
			for (int i = 0; i < card; ++i) {
				values[i] = (Writable)this.types[i+1].newInstance();
				values[i].readFields(in);
			}
		} catch (IllegalAccessException e) {
			throw (IOException)new IOException("Failed tuple init").initCause(e);
		} catch (InstantiationException e) {
			throw (IOException)new IOException("Failed tuple init").initCause(e);
		}
	}
}
