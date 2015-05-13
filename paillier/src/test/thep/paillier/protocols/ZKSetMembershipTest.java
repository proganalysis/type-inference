package test.thep.paillier.protocols;

import java.math.BigInteger;

import junit.framework.TestCase;
import thep.paillier.EncryptedInteger;
import thep.paillier.PrivateKey;
import thep.paillier.PublicKey;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;
import thep.paillier.exceptions.ZKSetMembershipException;
import thep.paillier.protocols.ZKSetMembershipProver;
import thep.paillier.protocols.ZKSetMembershipVerifier;

public class ZKSetMembershipTest extends TestCase {
	private PrivateKey priv;
	private PublicKey pub;
	
	public ZKSetMembershipTest(String name) {
		super(name);
	}
	
	protected void setUp() {
		this.priv = new PrivateKey(1024);
		this.pub = this.priv.getPublicKey();
	}
	
	public void testZKSMTrue() throws ZKSetMembershipException, BigIntegerClassNotValid {
		BigInteger[] theSet = {new BigInteger("0"), new BigInteger("1"), 
				new BigInteger("2"), new BigInteger("3"), new BigInteger("4")};
		
		EncryptedInteger c = new EncryptedInteger(pub);
		BigInteger r = c.set(BigInteger.ONE); // must call set to get r, need to fix this
		
		int msgIndex = 1;
		
		for (int i=0; i<10; i++) {
			// create the prover and generate the commitments
			ZKSetMembershipProver prover = new ZKSetMembershipProver(pub, theSet, msgIndex, c);
			BigInteger[] uVals = prover.genCommitments();
			
			// generate the verifier with the commitments and generate the challenge
			ZKSetMembershipVerifier verifier = new ZKSetMembershipVerifier(pub, c, uVals, theSet);
			BigInteger e = verifier.genChallenge(new BigInteger("128"));
			
			// Use the challenge to generate the response
			prover.computeResponse(e, r);
			BigInteger[] eVals = prover.getEs();
			BigInteger[] vVals = prover.getVs();
			
			// Check the responses
			assertTrue(verifier.checkResponse(eVals, vVals));
		}
	}
	
	public void testZKSMFalse() throws ZKSetMembershipException, BigIntegerClassNotValid {
		BigInteger[] theSet = {new BigInteger("0"), new BigInteger("1"), 
				new BigInteger("2"), new BigInteger("3"), new BigInteger("4")};
		EncryptedInteger c = new EncryptedInteger(new BigInteger("10"), pub);
		BigInteger r = c.set(new BigInteger("10"));
		int msgIndex = 2;
		
		for (int i=0; i<10; i++) {
			ZKSetMembershipProver prover = new ZKSetMembershipProver(pub, theSet, msgIndex, c);
			BigInteger[] uVals = prover.genCommitments();
			
			ZKSetMembershipVerifier verifier = new ZKSetMembershipVerifier(pub, c, uVals, theSet);
			BigInteger e = verifier.genChallenge(new BigInteger("128"));
			
			prover.computeResponse(e, r);
			BigInteger[] eVals = prover.getEs();
			BigInteger[] vVals = prover.getVs();
			
			assertFalse(verifier.checkResponse(eVals, vVals)); // TODO: This could actually be true with low probability
		}
	}
	
	public void testZKSMSmallSet() throws ZKSetMembershipException, BigIntegerClassNotValid {
		BigInteger[] theSet = {new BigInteger("0"), new BigInteger("1")};
		EncryptedInteger c = new EncryptedInteger(BigInteger.ONE, pub);
		BigInteger r = c.set(BigInteger.ONE);
		int msgIndex = 1;
		
		for (int i=0; i<10; i++) {
			ZKSetMembershipProver prover = new ZKSetMembershipProver(pub, theSet, msgIndex, c);
			BigInteger[] uVals = prover.genCommitments();
			
			ZKSetMembershipVerifier verifier = new ZKSetMembershipVerifier(pub, c, uVals, theSet);
			BigInteger e = verifier.genChallenge(new BigInteger("128"));
			
			prover.computeResponse(e, r);
			BigInteger[] eVals = prover.getEs();
			BigInteger[] vVals = prover.getVs();
			
			assertTrue(verifier.checkResponse(eVals, vVals));
		}
	}
	
	public void testZKSMSingleMemberSetTrue() throws ZKSetMembershipException, BigIntegerClassNotValid {
		BigInteger[] theSet = {new BigInteger("0")};
		EncryptedInteger c = new EncryptedInteger(BigInteger.ZERO, pub);
		BigInteger r = c.set(BigInteger.ZERO);
		int msgIndex = 0;
		
		for (int i=0; i<10; i++) {
			ZKSetMembershipProver prover = new ZKSetMembershipProver(pub, theSet, msgIndex, c);
			BigInteger[] uVals = prover.genCommitments();
			
			ZKSetMembershipVerifier verifier = new ZKSetMembershipVerifier(pub, c, uVals, theSet);
			BigInteger e = verifier.genChallenge(new BigInteger("128"));
			
			prover.computeResponse(e, r);
			BigInteger[] eVals = prover.getEs();
			BigInteger[] vVals = prover.getVs();
			
			assertTrue(verifier.checkResponse(eVals, vVals));
		}
	}
	
	public void testZKSMSingleMemberSetFalse() throws ZKSetMembershipException, BigIntegerClassNotValid {
		BigInteger[] theSet = {new BigInteger("0")};
		EncryptedInteger c = new EncryptedInteger(BigInteger.ONE, pub);
		BigInteger r = c.set(BigInteger.ONE);
		int msgIndex = 0;
		
		for (int i=0; i<10; i++) {
			ZKSetMembershipProver prover = new ZKSetMembershipProver(pub, theSet, msgIndex, c);
			BigInteger[] uVals = prover.genCommitments();
			
			ZKSetMembershipVerifier verifier = new ZKSetMembershipVerifier(pub, c, uVals, theSet);
			BigInteger e = verifier.genChallenge(new BigInteger("128"));
			
			prover.computeResponse(e, r);
			BigInteger[] eVals = prover.getEs();
			BigInteger[] vVals = prover.getVs();
			
			assertFalse(verifier.checkResponse(eVals, vVals)); // TODO: This could actually be true with low probability
		}
	}
	
	public void testZKSMAddTrue() throws ZKSetMembershipException, PublicKeysNotEqualException, BigIntegerClassNotValid {
		BigInteger[] theSet = {new BigInteger("0"), new BigInteger("1"), 
				new BigInteger("2"), new BigInteger("3"), new BigInteger("4"),
				new BigInteger("6")};
		EncryptedInteger c1 = new EncryptedInteger(new BigInteger("2"), pub);
		BigInteger r1 = c1.set(new BigInteger("2"));
		EncryptedInteger c2 = new EncryptedInteger(new BigInteger("3"), pub);
		BigInteger r2 = c2.set(new BigInteger("3"));
		
		EncryptedInteger c = c1.add(c2);
		
		BigInteger r = r1.multiply(r2).mod(this.pub.getNSquared());
		
		
		int msgIndex = 5;
		
		for (int i=0; i<10; i++) {
			ZKSetMembershipProver prover = new ZKSetMembershipProver(pub, theSet, msgIndex, c);
			BigInteger[] uVals = prover.genCommitments();
			
			ZKSetMembershipVerifier verifier = new ZKSetMembershipVerifier(pub, c, uVals, theSet);
			BigInteger e = verifier.genChallenge(new BigInteger("128"));
			
			prover.computeResponse(e, r);
			BigInteger[] eVals = prover.getEs();
			BigInteger[] vVals = prover.getVs();
			
			assertFalse(verifier.checkResponse(eVals, vVals)); // TODO: This could actually be true with low probability
		}
	}
	
	public void testZKSMManyOperations() throws ZKSetMembershipException, PublicKeysNotEqualException, BigIntegerClassNotValid {
		BigInteger[] theSet = {new BigInteger("0"), new BigInteger("1"), 
				new BigInteger("2"), new BigInteger("3"), new BigInteger("4"),
				new BigInteger("6")};
		EncryptedInteger c1 = new EncryptedInteger(new BigInteger("2"), pub);
		BigInteger r1 = c1.set(new BigInteger("2"));
		EncryptedInteger c2 = new EncryptedInteger(new BigInteger("3"), pub);
		BigInteger r2 = c2.set(new BigInteger("3"));
		
		EncryptedInteger c = c1.add(c2);
		
		BigInteger r = r1.multiply(r2).mod(this.pub.getNSquared());
		
		
		int msgIndex = 5;
		
		for (int i=0; i<10; i++) {
			ZKSetMembershipProver prover = new ZKSetMembershipProver(pub, theSet, msgIndex, c);
			BigInteger[] uVals = prover.genCommitments();
			
			ZKSetMembershipVerifier verifier = new ZKSetMembershipVerifier(pub, c, uVals, theSet);
			BigInteger e = verifier.genChallenge(new BigInteger("128"));
			
			prover.computeResponse(e, r);
			BigInteger[] eVals = prover.getEs();
			BigInteger[] vVals = prover.getVs();
			
			assertFalse(verifier.checkResponse(eVals, vVals)); // TODO: This could actually be true with low probability
		}
	}
	
	public void testZKSMVectorTrue() throws ZKSetMembershipException, PublicKeysNotEqualException, BigIntegerClassNotValid {
		BigInteger[] theSet = {BigInteger.ZERO, BigInteger.ONE};
		BigInteger[] theSetSingle = {BigInteger.ONE};
		
		BigInteger[] theVector = {BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO};
		EncryptedInteger[] theEncryptedVector = new EncryptedInteger[theVector.length];
		BigInteger[] rVector = new BigInteger[theVector.length];
		// initialize the encrypted vector
		for (int i=0; i<theVector.length; i++) {
			EncryptedInteger tmp = new EncryptedInteger(pub);
			rVector[i] = tmp.set(theVector[i]);
			theEncryptedVector[i] = tmp;
		}
		
		// prove each element is in theSet
		for (int i=0; i<theEncryptedVector.length; i++) {
			int msgIndex = theVector[i].equals(BigInteger.ZERO) ? 0 : 1;
			ZKSetMembershipProver prover = new ZKSetMembershipProver(pub, theSet, msgIndex, theEncryptedVector[i]);
			BigInteger[] uVals = prover.genCommitments();
			
			ZKSetMembershipVerifier verifier = new ZKSetMembershipVerifier(pub, theEncryptedVector[i], uVals, theSet);
			BigInteger e = verifier.genChallenge(new BigInteger("128"));
			
			prover.computeResponse(e, rVector[i]);
			BigInteger[] eVals = prover.getEs();
			BigInteger[] vVals = prover.getVs();
			
			assertTrue(verifier.checkResponse(eVals, vVals));
		}
		
		// prove that the sum is in theSingleSet
		
		// accumulate the r's
		BigInteger accumR = BigInteger.ONE;
		for (BigInteger r : rVector) {
			accumR = accumR.multiply(r).mod(this.pub.getNSquared());
		}
		
		// sum the vector
		EncryptedInteger sum = theEncryptedVector[0];
		for (int i=1; i<theEncryptedVector.length; i++) {
			sum = sum.add(theEncryptedVector[i]);
		}
		
		ZKSetMembershipProver prover = new ZKSetMembershipProver(pub, theSetSingle, 0, sum);
		BigInteger[] uVals = prover.genCommitments();
		
		ZKSetMembershipVerifier verifier = new ZKSetMembershipVerifier(pub, sum, uVals, theSetSingle);
		BigInteger e = verifier.genChallenge(new BigInteger("128"));
		
		prover.computeResponse(e, accumR);
		BigInteger[] eVals = prover.getEs();
		BigInteger[] vVals = prover.getVs();
		
		assertTrue(verifier.checkResponse(eVals, vVals));
	}
	
	public void testZKSMNonInteractiveTrue() throws ZKSetMembershipException, BigIntegerClassNotValid {
		BigInteger[] theSet = {new BigInteger("0"), new BigInteger("1"), 
				new BigInteger("2"), new BigInteger("3"), new BigInteger("4")};
		
		EncryptedInteger c = new EncryptedInteger(pub);
		BigInteger r = c.set(BigInteger.ONE); // must call set to get r, need to fix this securely somehow
		
		int msgIndex = 1;
		
		for (int i=0; i<10; i++) {
			// Run non-interactive prover
			ZKSetMembershipProver prover = new ZKSetMembershipProver(pub, theSet, msgIndex, c);
			BigInteger[] uVals = prover.genCommitments();
			BigInteger e = prover.genChallengeFromCommitments(uVals);
			prover.computeResponse(e, r);
			BigInteger[] eVals = prover.getEs();
			BigInteger[] vVals = prover.getVs();
			
			// run non-interactive verifier
			ZKSetMembershipVerifier verifier = new ZKSetMembershipVerifier(pub, c, uVals, theSet);
			assertTrue(verifier.checkResponseNonInteractive(eVals, vVals, e));
		}
	}
}
