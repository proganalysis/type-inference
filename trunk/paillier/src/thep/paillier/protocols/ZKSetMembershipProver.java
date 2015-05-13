package thep.paillier.protocols;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import thep.paillier.EncryptedInteger;
import thep.paillier.PublicKey;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.ZKSetMembershipException;

public class ZKSetMembershipProver {
	// class members
	private BigInteger[] eVals;
	private BigInteger[] vVals;
	private PublicKey pub;
	private BigInteger[] theSet;
	private int msgIndex;
	private EncryptedInteger cipherVal;
	private BigInteger rho;
	private Random rng;
	private MessageDigest hashFunc;
	@SuppressWarnings("rawtypes")
	private Constructor rngCons;
	@SuppressWarnings("rawtypes")
	private Constructor biCons;

	/**
	 * Constructs the class using BigInteger for big integers
	 * @param pub the public key
	 * @param theSet the set we want to prove cipherVal is in
	 * @param msgIndex the index we are claiming cipherVal is in theSet
	 * @param cipherVal the cipher text for the proof
	 * @throws ZKSetMembershipException
	 * @throws BigIntegerClassNotValid 
	 */
	public ZKSetMembershipProver(PublicKey pub, BigInteger[] theSet, 
			int msgIndex, EncryptedInteger cipherVal) throws ZKSetMembershipException, BigIntegerClassNotValid {
		this(pub, theSet, msgIndex, cipherVal, BigInteger.class);
	}
	
	/**
	 * A constructor which includes setting a class for big integers
	 * @param pub the public key
	 * @param theSet the set we want to prove cipherVal is in
	 * @param msgIndex the index we are claiming cipherVal is in theSet
	 * @param cipherVal the cipher text for the proof
	 * @param c the class to use for big integers
	 * @throws ZKSetMembershipException 
	 * @throws BigIntegerClassNotValid 
	 */
	public ZKSetMembershipProver(PublicKey pub, BigInteger[] theSet, 
			int msgIndex, EncryptedInteger cipherVal,
			Class<? extends BigInteger> c) throws ZKSetMembershipException, BigIntegerClassNotValid {
		// intialize members
		this.pub = pub;
		this.theSet = theSet;
		this.msgIndex = msgIndex;
		this.cipherVal = cipherVal;
		this.rngCons = this.findRngCons(c);
		this.biCons = this.findBICons(c);
		
		// create the secure random number generator
		this.rng = new SecureRandom();
		
		// set the hash function
		try {
			this.hashFunc = java.security.MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new ZKSetMembershipException("Could not initialize the hash function for non-interactive mode");
		}
		
		// set to null for error checking.
		this.rho = null;
	}
	
	/**
	 * Generates the commitments for proving that the cipher text is in the given set.
	 * The commitments are returned and should be sent to the verifier.
	 *
	 * @return the commitments
	 * @throws ZKSetMembershipException
	 * @throws BigIntegerClassNotValid 
	 */
	public BigInteger[] genCommitments() throws ZKSetMembershipException, BigIntegerClassNotValid {
		int setLen = theSet.length;
		BigInteger[] commitments = new BigInteger[setLen];
		BigInteger N = this.pub.getN();
		BigInteger N_squared = this.pub.getNSquared();
		BigInteger c = cipherVal.getCipherVal();
		BigInteger c_inverse = c.modInverse(N_squared);
		BigInteger g = this.pub.getG();
		int bits = this.pub.getBits();
		
		if (msgIndex >= setLen || msgIndex < 0) { // check the input data
			throw new ZKSetMembershipException("Index out of Range");
		}
		
		// if necessary create values with given class
		if (this.biCons != null) {
			try {
				g = (BigInteger) this.biCons.newInstance(g);
			} catch (Exception e) {
				throw new BigIntegerClassNotValid("Could not construct");
			}
		}
		
		// generate a random rho
		this.rho = new BigInteger(bits, this.rng);
		// rho needs to be less than N, but not zero
		while (rho.compareTo(N) > 0 || rho.compareTo(BigInteger.ZERO) == 0) {
			rho = new BigInteger(bits, this.rng);
		}
		
		this.eVals = new BigInteger[setLen];
		for (int i=0; i<setLen; i++) {
			// generate random e value
			this.eVals[i] = new BigInteger(bits, this.rng);
			// the e value must be less than n
			while (this.eVals[i].compareTo(N) > 0) {
				this.eVals[i] = new BigInteger(bits, this.rng);
			}
		}
		
		this.vVals = new BigInteger[setLen];
		for (int i=0; i<setLen; i++) {
			// generate random v value
			try {
				this.vVals[i] = (BigInteger) this.rngCons.newInstance(bits, this.rng);
				
				// the v value must be less than n and not 0
				while (this.vVals[i].compareTo(N) > 0 || this.vVals[i].compareTo(BigInteger.ZERO) == 0) {
					this.vVals[i] = (BigInteger) this.rngCons.newInstance(bits, this.rng);
				}
			} catch (Exception e) {
				throw new BigIntegerClassNotValid("Could not construct");
			}
		}
		
		// calculate the commitments
		for (int i=0; i<setLen; i++) {
			if (i == msgIndex) {
				BigInteger tmpRho = rho;
				
				if (this.biCons != null)
					try {
						tmpRho = (BigInteger) this.biCons.newInstance(rho);
					} catch (Exception e) {
						throw new BigIntegerClassNotValid("Could not construct");
					}
				
				commitments[i] = tmpRho.modPow(N, N_squared);
			}
			else {
				BigInteger tmp1 = vVals[i].modPow(N, N_squared);
				BigInteger tmp2 = g.modPow(theSet[i], N_squared);
				
				tmp2 = tmp2.multiply(c_inverse);
				
				if (this.biCons != null)
					try {
						tmp2 = (BigInteger) this.biCons.newInstance(tmp2);
					} catch (Exception e) {
						throw new BigIntegerClassNotValid("Could not construct");
					}
				
				tmp2 = tmp2.modPow(eVals[i], N_squared);
				commitments[i] = tmp1.multiply(tmp2);
				commitments[i] = commitments[i].mod(N_squared);
			}
		}
		
		return commitments;
	}
	
	/**
	 * Uses the idea behind the Fiat-Shamir paradigm to generate the challenge for a non-interactive proof
	 * 
	 * @param uVals the commitments generated by the prover
	 * @param A the challenge length (should be agreed upon in advance with the verifier).
	 * The probability that a prover can successfully cheat is 1/A. A should be a power of 2.
	 * @return the challenge
	 */
	public BigInteger genChallengeFromCommitments(BigInteger[] uVals, BigInteger A) {
		if (uVals.length == 1) {
			throw new IllegalArgumentException("The supplied array must have at least one value");
		}
		
		for (int i=0; i<uVals.length; i++) {
			this.hashFunc.update(uVals[i].toByteArray());
		}
		
		// Return the digest (digest() calls reset())
		return new BigInteger(this.hashFunc.digest()).mod(A);
	}
	
	/**
	 * Calls genChallengeFromCommitments(BigInteger[] uVals, BigInteger A) with A equal to 128 
	 * @param uVals the commitments generated by the prover
	 * @return the challenge
	 */
	public BigInteger genChallengeFromCommitments(BigInteger[] uVals) {
		return genChallengeFromCommitments(uVals, new BigInteger("128"));
	}
	
	/**
	 * Computes the provers response to the challenge e. The prover should
	 * then use getVs() and getEs() and send those values to the verifier.
	 * 
	 * @param e the challenge
	 * @param r the random number used during encryption
	 * @throws ZKSetMembershipException 
	 * @throws BigIntegerClassNotValid 
	 */
	public void computeResponse(BigInteger e, BigInteger r) throws ZKSetMembershipException, BigIntegerClassNotValid {
		if (this.rho == null) {
			throw new ZKSetMembershipException("genCommitments() must be called before computeResponse()");
		}
		
		BigInteger N = this.pub.getN();
		BigInteger g = this.pub.getG();
		
		if (this.biCons != null) {
			try {
			r = (BigInteger) this.biCons.newInstance(r);
			g = (BigInteger) this.biCons.newInstance(g);
			}
			catch (Exception ex) {
				throw new BigIntegerClassNotValid("Could not construct");
			}
		}
		
		// compute e_i
		BigInteger tmp1 = e;
		for (int i=0; i<this.eVals.length; i++) {
			if (i != msgIndex) {
				tmp1 = tmp1.subtract(this.eVals[i]);
			}
		}
		BigInteger e_i = tmp1.mod(N);
		this.eVals[msgIndex] = e_i;
		
		// compute v_i
		BigInteger v_i = this.rho.multiply(r.modPow(e_i, N)).mod(N);
		tmp1 = tmp1.divide(N);
		tmp1 = g.modPow(tmp1, N);
		v_i = v_i.multiply(tmp1).mod(N);
		this.vVals[msgIndex] = v_i;
	}
	
	/**
	 * The V values needed for the last part of the proof
	 * @return the v values
	 */
	public BigInteger[] getVs() {
		return this.vVals;
	}
	
	/**
	 * The E values needed for the last part of the proof
	 * @return the e values
	 */
	public BigInteger[] getEs() {
		return this.eVals;
	}
	
	@SuppressWarnings("rawtypes")
	private Constructor findRngCons(Class<? extends BigInteger> c) throws BigIntegerClassNotValid {
		Constructor cons = null;
		for (Constructor i : c.getConstructors()) {
			Class[] params = i.getParameterTypes();
			if (params.length == 2 && params[0].getCanonicalName().equals("int") &&
					params[1].getCanonicalName().equals("java.util.Random")) {
				cons = i;
				break;
			}
		}
		
		if (cons == null) {
			throw new BigIntegerClassNotValid("Could not find the int, Random constructor");
		}
		
		return cons;
	}
	
	@SuppressWarnings("rawtypes")
	private Constructor findBICons(Class<? extends BigInteger> c) {
		Constructor cons = null;
		for (Constructor i : c.getConstructors()) {
			Class[] params = i.getParameterTypes();
			if (params.length == 1 && params[0].getCanonicalName().equals("java.math.BigInteger")) {
				cons = i;
				break;
			}
		}
		
		return cons;
	}
}
