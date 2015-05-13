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

public class ZKSetMembershipVerifier {
	// class members
	private BigInteger[] uVals;
	private BigInteger e;
	private PublicKey pub;
	private EncryptedInteger cipherVal;
	private BigInteger[] theSet;
	private MessageDigest hashFunc;
	@SuppressWarnings("rawtypes")
	private Constructor biCons;

	/**
	 * Constructor which uses BigInteger
	 * 
	 * @param pub the public key
	 * @param cipherVal the cipher text
	 * @param uVals the u values from the prover
	 * @param theSet the set on which to test membership
	 * @throws ZKSetMembershipException
	 * @throws BigIntegerClassNotValid
	 */
	public ZKSetMembershipVerifier(PublicKey pub, EncryptedInteger cipherVal, 
			BigInteger[] uVals,	BigInteger[] theSet) throws ZKSetMembershipException, BigIntegerClassNotValid {
		this(pub, cipherVal, uVals, theSet, BigInteger.class);
	}
	
	/**
	 * Constructor
	 * 
	 * @param pub the public key
	 * @param cipherVal the cipher text
	 * @param uVals the u values from the prover
	 * @param theSet the set on which to test membership
	 * @param c the class to use for big integers
	 * @throws ZKSetMembershipException 
	 * @throws BigIntegerClassNotValid 
	 */
	public ZKSetMembershipVerifier(PublicKey pub, EncryptedInteger cipherVal, 
			BigInteger[] uVals,	BigInteger[] theSet, Class<? extends BigInteger> c) throws ZKSetMembershipException, BigIntegerClassNotValid {
		this.pub = pub;
		this.cipherVal = cipherVal;
		this.uVals = uVals;
		this.theSet = theSet;
		this.biCons = this.findBICons(c);
		
		try {
			this.hashFunc = java.security.MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new ZKSetMembershipException("Could not initialize the hash function for non-interactive mode");
		}
	}
	
	/**
	 * Generates the challenge for the prover
	 * 
	 * @param A the upper bound on the challenge. The probability that
	 * a malicious prover can complete the protocol is 1/A. Multiple
	 * rounds of the protocol can be run for stronger assurances. This
	 * step in the protocol can be replaced using the Fiat-Shamir paradigm
	 * to make the zero knowledge proof non-interactive.
	 * @return the challenge
	 */
	public BigInteger genChallenge(BigInteger A) {
		Random rng = new SecureRandom();
		
		// generate a random challenge
		this.e = new BigInteger(A.bitLength(), rng);
		// Make sure the random challenge is less than A
		while (this.e.compareTo(A) > 0) {
			this.e = new BigInteger(A.bitLength(), rng);
		}
		
		return this.e;
	}
	
	/**
	 * Checks the response from the prover
	 * 
	 * @param eVals the e values given by the prover
	 * @param vVals the v values given by the prover
	 * @return true if the response check is OK, otherwise false
	 * @throws ZKSetMembershipException
	 * @throws BigIntegerClassNotValid 
	 */
	public boolean checkResponse(BigInteger[] eVals, BigInteger[] vVals) throws ZKSetMembershipException, BigIntegerClassNotValid {
		if (eVals.length != vVals.length) {
			throw new ZKSetMembershipException("Arrays passed to checkResponse must be same length");
		}
		
		BigInteger eValAccum = BigInteger.ZERO;
		BigInteger N_Squared = this.pub.getNSquared();
		
		for (BigInteger tmp : eVals) {
			eValAccum = eValAccum.add(tmp).mod(this.pub.getN());
		}
		
		if (this.e.compareTo(eValAccum) != 0) {
			return false;
		}
		
		for (int i=0; i<eVals.length; i++) {
			BigInteger tmpV = vVals[i];
			BigInteger g = this.pub.getG();
			
			if (this.biCons != null) {
				try {
					tmpV = (BigInteger) this.biCons.newInstance(tmpV);
					g = (BigInteger) this.biCons.newInstance(g);
				}
				catch (Exception e) {
					throw new BigIntegerClassNotValid("Could not construct");
				}
			}
			
			BigInteger lhs = tmpV.modPow(this.pub.getN(), N_Squared);
			BigInteger rhs = g.modPow(this.theSet[i], N_Squared);
			rhs = rhs.modInverse(N_Squared);
			rhs = rhs.multiply(this.cipherVal.getCipherVal()).mod(N_Squared);
			
			if (this.biCons != null)
				try {
					rhs = (BigInteger) this.biCons.newInstance(rhs);
				}
				catch (Exception e){
					throw new BigIntegerClassNotValid("Could not construct");
				}
			
			rhs = rhs.modPow(eVals[i], N_Squared);
			rhs = rhs.multiply(this.uVals[i]).mod(N_Squared);
			
			if (lhs.compareTo(rhs) != 0) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks the response from the prover. Uses the Fiat-Shamir heuristic
	 * 
	 * @param eVals the e values given by the prover
	 * @param vVals the v values given by the prover
	 * @param challenge the challenge value used by the prover
	 * @param A the length of the challenge agreed upon by the two parties, should be a power of 2
	 * @return true if the response check is OK, otherwise false
	 * @throws ZKSetMembershipException
	 * @throws BigIntegerClassNotValid 
	 */
	public boolean checkResponseNonInteractive(BigInteger[] eVals, BigInteger[] vVals, 
			BigInteger challenge, BigInteger A) throws ZKSetMembershipException, BigIntegerClassNotValid {
		// make sure lengths are equal
		if (eVals.length != vVals.length) {
			throw new ZKSetMembershipException("Arrays passed to checkResponse must be same length");
		}
		
		// check the given digest
		for (int i=0; i<this.uVals.length; i++) {
			this.hashFunc.update(this.uVals[i].toByteArray());
		}
		
		BigInteger testDigest = new BigInteger(this.hashFunc.digest()).mod(A);
		
		if (!testDigest.equals(challenge))	{
			return false;
		}
		
		this.e = challenge;
		
		
		return this.checkResponse(eVals, vVals);
	}

	/**
	 * Checks the response from the prover. Uses the Fiat-Shamir heuristic.
	 * Sets A equal to 128.
	 * 
	 * @param eVals the e values given by the prover
	 * @param vVals the v values given by the prover
	 * @param challenge the challenge value used by the prover
	 * @return true if the response check is OK, otherwise false
	 * @throws ZKSetMembershipException
	 * @throws BigIntegerClassNotValid 
	 */
	public boolean checkResponseNonInteractive(BigInteger[] eVals, BigInteger[] vVals, 
			BigInteger challenge) throws ZKSetMembershipException, BigIntegerClassNotValid {
		return checkResponseNonInteractive(eVals, vVals, challenge, new BigInteger("128"));
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
