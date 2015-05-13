package thep.paillier;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import thep.paillier.exceptions.BigIntegerClassNotValid;

public class PrivateKey implements Serializable {
	/**
	 * Serial version id
	 */
	private static final long serialVersionUID = 1L;
	private PublicKey pub;
	private BigInteger lambda;
	private BigInteger mu;
	
	/**
	 * Constructs a private key with the specified number of bits
	 * 
	 * @param bits the number of bits for the key
	 * @throws BigIntegerClassNotValid 
	 */
	public PrivateKey(int bits) {		
		// Create a secure random number generator
		Random rng = new SecureRandom();

		// Generate the random primes
		BigInteger p = new BigInteger(bits/2, 10, rng);
		BigInteger q = new BigInteger(bits/2, 10, rng);
		
		// Compute values
		BigInteger n = p.multiply(q);
		p = p.subtract(BigInteger.ONE); // p is now p-1
		q = q.subtract(BigInteger.ONE); // q is now q-1
		this.lambda = p.multiply(q); // (p-1) * (q-1)
		
		// Set values
		this.pub = new PublicKey(bits, n);
		this.mu = this.lambda.modInverse(this.pub.getN());
	}
	
	/**
	 * Returns lambda which is calculated as LCM(p-1,q-1)
	 * 
	 * @return the value for lambda, or LCM(p-1,q-1)
	 */
	public BigInteger getLambda() {
		return lambda;
	}
	
	/**
	 * Return a public key associated with this private key
	 * 
	 * @return the public key associated with this private key
	 */
	public PublicKey getPublicKey() {
		return pub;
	}
	
	/*
	 * The value x is precomputed only for efficient computations, and
	 * should not be used outside of this package.
	 */
	BigInteger getMu() {
		return mu;
	}
}
