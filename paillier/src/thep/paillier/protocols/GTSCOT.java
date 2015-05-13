package thep.paillier.protocols;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import thep.paillier.EncryptedInteger;
import thep.paillier.PrivateKey;
import thep.paillier.PublicKey;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;
import thep.paillier.exceptions.SizesNotEqualException;

public class GTSCOT {
	/**
	 * Performs the senders function of the GT-SCOT protocol.
	 * 
	 * @param pub the public key used for the encrypted integers
	 * @param x the encrypted input vector from the receiver
	 * @param y the senders unencrypted input vector
	 * @param s0 message to send if x<y, should be member of D_s
	 * @param s1 message to send if x>y, should be member of D_s
	 * @return an encrypted vector which contains s0 if x<y, s1 if x>y
	 * @throws SizesNotEqualException
	 * @throws PublicKeysNotEqualException
	 * @throws BigIntegerClassNotValid 
	 */
	public static EncryptedInteger[] sender(PublicKey pub, EncryptedInteger[] x, 
			BigInteger[] y, BigInteger s0, BigInteger s1) 
	  throws SizesNotEqualException, PublicKeysNotEqualException, BigIntegerClassNotValid {
		// Check bit lengths of inputs, make sure they are equal
		// pad the shorter vector if necessary
		if (x.length < y.length) {
			EncryptedInteger zero = new EncryptedInteger(BigInteger.ZERO, pub);
			int pad_len = y.length - x.length;
			EncryptedInteger[] tmp = new EncryptedInteger[y.length];
			for (int i=0; i<tmp.length; i++) {
				if (i<pad_len)
					tmp[i] = zero;
				else
					tmp[i] = x[i-pad_len];
			}
			
			x = tmp;
		}
		else if (x.length > y.length) {
			BigInteger zero = BigInteger.ZERO;
			int pad_len = x.length - y.length;
			BigInteger[] tmp = new BigInteger[x.length];
			for (int i=0; i<tmp.length; i++) {
				if (i<pad_len)
					tmp[i] = zero;
				else
					tmp[i] = y[i-pad_len];
			}
			
			y = tmp;
		}
		// Check public keys to make sure everything is encrypted
		// with the same key.
		for (EncryptedInteger e : x)
			if (!e.getPublicKey().equals(pub))
				throw new PublicKeysNotEqualException("All values in encrypted " +
						"vector must be encrypted with same public key");
		
		// compute d
		EncryptedInteger[] d = new EncryptedInteger[x.length];
		for (int i=0; i<d.length; i++) {
			d[i] = x[i].add(y[i].negate());
		}
		
		// compute f
		EncryptedInteger[] f = new EncryptedInteger[x.length];
		for (int i=0; i<f.length; i++) {
			f[i] = GTSCOT.computeF(x[i], y[i]);
		}
		
		// compute gamma
		EncryptedInteger[] gamma = new EncryptedInteger[x.length+1];
		gamma[0] = new EncryptedInteger(BigInteger.ZERO, pub);
		for (int i=1; i<gamma.length; i++) {
			gamma[i] = gamma[i-1].multiply(new BigInteger("2")).add(f[i-1]);
		}
		
		// compute delta
		Random rng = new SecureRandom();
		BigInteger m_one = BigInteger.ONE.negate();
		EncryptedInteger[] delta = new EncryptedInteger[x.length];
		for (int i=0; i<delta.length; i++) {
			BigInteger r = new BigInteger(pub.getBits(), rng);
			delta[i] = d[i].add(gamma[i+1].add(m_one).multiply(r));
		}
		
		// compute mu
		EncryptedInteger[] mu = new EncryptedInteger[x.length];
		// We don't do the division by two as described in the protocol since
		// if part1 and part2 are odd, division causes problems with rounding.
		// Instead we handle this at the receivers end.
		BigInteger part1 = s1.subtract(s0);
		BigInteger part2 = s1.add(s0);
		
		for (int i=0; i<mu.length; i++) {
			mu[i] = delta[i].multiply(part1).add(part2);
		}
		
		// permute mu
		List<EncryptedInteger> tmp = Arrays.asList(mu);
		Collections.shuffle(tmp);
		mu = (EncryptedInteger[]) tmp.toArray();
		
		// return mu
		return mu;
	}
	
	/**
	 * Performs the receiver function of the GT-SCOT protocol
	 * 
	 * @param priv the private key for decryption
	 * @param mu the encrypted vector output by the sender
	 * @param max the max value used to determine membership in D_s, if 2^N
	 * is plaintext message space, max should be fairly close (40 to 80 bits
	 * smaller) to maximize bandwidth. It should not be same as plaintext
	 * message space however, or the protocol won't work.
	 * @return returns the decryption of the answer from the server
	 * @throws BigIntegerClassNotValid 
	 */
	public static BigInteger receiver(PrivateKey priv, EncryptedInteger[] mu, 
			BigInteger max) throws BigIntegerClassNotValid {
		BigInteger rval = null;
		BigInteger two = new BigInteger("2");
		for (EncryptedInteger i : mu) {
			// Divide the decrypted value by two since we don't divide by 2
			// during calculation at sender's end.
			BigInteger tmp = i.decrypt(priv).divide(two);
			if (tmp.compareTo(max) < 0)
				if (rval == null)
					// we found a potential answer
					rval = tmp;
				else {
					// there are multiple possible decryptions
					rval = null;
					break;
				}
		}
		
		return rval;
	}
	
	// Helper functions
	/**
	 * Creates a vector which represents the given number
	 * @param y the number from which to create the vector
	 * @return an array containing the individual bits of y encrypted separately (from MSBit to LSBit)
	 */
	public static BigInteger[] createVector(BigInteger y) {
		BigInteger[] tmp = new BigInteger[y.bitLength()];
		
		// Add values to tmp, from MSBit to LSBit
		for (int i=y.bitLength(); i>0; i--) {
			tmp[tmp.length-i] = y.testBit(i-1) ? BigInteger.ONE : BigInteger.ZERO;
		}
		
		return tmp;
	}
	
	/**
	 * Creates an encrypted vector
	 * @param pub the public key used for encryption
	 * @param x the number to use to create the vector
	 * @return the encrypted vector
	 * @throws BigIntegerClassNotValid 
	 */
	public static EncryptedInteger[] createEncryptedVector(PublicKey pub, 
			BigInteger x) throws BigIntegerClassNotValid {
		BigInteger[] tmp = GTSCOT.createVector(x);
		EncryptedInteger[] encrypted_tmp = new EncryptedInteger[tmp.length];
		
		for (int i=0; i<tmp.length; i++) {
			encrypted_tmp[i] = new EncryptedInteger(tmp[i], pub);
		}
		
		return encrypted_tmp;
	}
	
	private static EncryptedInteger computeF(EncryptedInteger xi, BigInteger yi) throws PublicKeysNotEqualException, BigIntegerClassNotValid {
		EncryptedInteger tmp = null;
		//tmp = xi.add(xi.multiply(yi).multiply(new BigInteger("-2"))).add(yi);

        if (yi.equals(BigInteger.ONE)) {
            tmp = xi.multiply(new BigInteger("-1")).add(BigInteger.ONE);
        }
        else {
            tmp = xi;
        }
		
		return tmp;
	}
}
