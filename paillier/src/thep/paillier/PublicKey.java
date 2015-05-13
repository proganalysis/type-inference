package thep.paillier;

import java.io.Serializable;
import java.math.BigInteger;

public class PublicKey implements Serializable {
	/**
	 * The serial version ID
	 */
	private static final long serialVersionUID = 1L;
	private int bits;
	private BigInteger n;
	private BigInteger n_squared;
	private BigInteger g; // = n+1
	
	/**
	 * Constructs a public key with the given modulus. Precomputes a few values
	 * for more efficient computations.
	 * 
	 * @param bits the number of bits in the key
	 * @param n the public modulus
	 */
	public PublicKey(int bits, BigInteger n) {
		this.bits = bits;
		this.n = n;
		this.n_squared = n.multiply(n);
		this.g = n.add(BigInteger.ONE);
	}
	
	/**
	 * Returns the public modulus
	 * @return the public modulus
	 */
	public BigInteger getN() {
		return n;
	}
	
	/**
	 * Returns the number of bits in the key
	 * @return the number of bits in the key
	 */
	public int getBits() {
		return bits;
	}
	
	/**
	 * Determines if two public keys are equal based on the value of lambda
	 * @param other the other public key to look at
	 * @return true if they are equal, false otherwise
	 */
	public boolean equals(PublicKey other) {
		return this.n.equals(other.getN());
	}
	
	/**
	 * Returns g
	 * @return g
	 */
	public BigInteger getG() {
		return g;
	}
	
	/**
	 * Returns N Squared
	 * @return N Squared
	 */
	public BigInteger getNSquared() {
		return n_squared;
	}
}
