package test.thep.paillier.protocols;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Random;

import junit.framework.TestCase;
import thep.paillier.EncryptedInteger;
import thep.paillier.PrivateKey;
import thep.paillier.PublicKey;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;
import thep.paillier.exceptions.SizesNotEqualException;
import thep.paillier.protocols.GTSCOT;

public class GTSCOTTest extends TestCase {
	private PrivateKey priv;
	private PublicKey pub;
	private Random rng;
	
	public GTSCOTTest(String name) {
		super(name);
	}
	
	protected void setUp() {
		priv = new PrivateKey(1024);
		pub = priv.getPublicKey();
		rng = new Random();
	}

	/*
	 * Test the greater than case
	 */
	public void testGT() throws SizesNotEqualException, PublicKeysNotEqualException, BigIntegerClassNotValid {
		// Set up values
		BigInteger x = new BigInteger("34");
		BigInteger y = new BigInteger("21");
		BigInteger max = new BigInteger("2").pow(901);
		BigInteger s0 = new BigInteger(900, rng).mod(pub.getN());
		BigInteger s1 = new BigInteger(900, rng).mod(pub.getN());
		
		// Prepare x and y
		EncryptedInteger[] x_vector = GTSCOT.createEncryptedVector(pub, x);
		BigInteger[] y_vector = GTSCOT.createVector(y);
		
		// Run Sender
		EncryptedInteger[] mu = GTSCOT.sender(pub, x_vector, y_vector, s0, s1);
		
		// Run Receiver
		BigInteger s = GTSCOT.receiver(priv, mu, max);
		
		assertEquals(s1, s);
	}
	
	/*
	 * Test the less than case
	 */
	public void testLT() throws SizesNotEqualException, PublicKeysNotEqualException, BigIntegerClassNotValid {
		// Set up values
		BigInteger x = new BigInteger("12");
		BigInteger y = new BigInteger("52");
		BigInteger max = new BigInteger("2").pow(901);
		BigInteger s0 = new BigInteger(900, rng).mod(pub.getN());
		BigInteger s1 = new BigInteger(900, rng).mod(pub.getN());
		
		// Prepare x and y
		EncryptedInteger[] x_vector = GTSCOT.createEncryptedVector(pub, x);
		BigInteger[] y_vector = GTSCOT.createVector(y);
		
		// Run Sender
		EncryptedInteger[] mu = GTSCOT.sender(pub, x_vector, y_vector, s0, s1);
		
		// Run Receiver
		BigInteger s = GTSCOT.receiver(priv, mu, max);
		
		assertEquals(s0, s);
	}
	
	/*
	 * Test the equal case
	 */
	public void testEQ() throws SizesNotEqualException, PublicKeysNotEqualException, BigIntegerClassNotValid {
		// Set up values
		BigInteger x = new BigInteger("13");
		BigInteger y = new BigInteger("13");
		BigInteger max = new BigInteger("2").pow(901);
		BigInteger s0 = new BigInteger(900, rng).mod(pub.getN());
		BigInteger s1 = new BigInteger(900, rng).mod(pub.getN());
		
		// Prepare x and y
		EncryptedInteger[] x_vector = GTSCOT.createEncryptedVector(pub, x);
		BigInteger[] y_vector = GTSCOT.createVector(y);
		
		// Run Sender
		EncryptedInteger[] mu = GTSCOT.sender(pub, x_vector, y_vector, s0, s1);
		
		// Run Receiver
		BigInteger s = GTSCOT.receiver(priv, mu, max);
		
		assertNull(s);
	}
	
	/*
	 * Test serialization of encrypted integer array
	 */
	public void testSerialization() throws IOException, SizesNotEqualException, PublicKeysNotEqualException, ClassNotFoundException, BigIntegerClassNotValid {
		// Set up values
		BigInteger x = new BigInteger("5");
		BigInteger y = new BigInteger("21");
		BigInteger max = new BigInteger("2").pow(901);
		BigInteger s0 = new BigInteger(900, rng).mod(pub.getN());
		BigInteger s1 = new BigInteger(900, rng).mod(pub.getN());
		
		// Prepare x and y
		EncryptedInteger[] x_vector = GTSCOT.createEncryptedVector(pub, x);
		BigInteger[] y_vector = GTSCOT.createVector(y);
		
		// Save the integer to an output stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(x_vector);
		
		// Load a new object from the output stream
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		EncryptedInteger[] xS_vector = (EncryptedInteger[])ois.readObject();
		
		// Run Sender
		EncryptedInteger[] mu = GTSCOT.sender(pub, xS_vector, y_vector, s0, s1);
		
		// Run Receiver
		BigInteger s = GTSCOT.receiver(priv, mu, max);
		
		assertEquals(s0, s);
	}
}
