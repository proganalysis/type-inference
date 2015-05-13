package test.thep.paillier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import junit.framework.TestCase;
import thep.paillier.EncryptedInteger;
import thep.paillier.EncryptedPolynomial;
import thep.paillier.PrivateKey;
import thep.paillier.PublicKey;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;
import thep.paillier.exceptions.SizesNotEqualException;

public class EncryptedPolynomialTest extends TestCase {
	private PrivateKey priv;
	private PublicKey pub;
	private EncryptedPolynomial identity;
	private EncryptedPolynomial square;
	private EncryptedPolynomial square_plus10;
	
	public EncryptedPolynomialTest(String name) {
		super(name);
	}
	
	protected void setUp() {
		priv = new PrivateKey(1024);
		pub = priv.getPublicKey();
		
		// Set up identity polynomial
		BigInteger[] id = {BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO};
		
		// Set up square polynomial
		BigInteger[] sq = {BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE};
		
		// Set up square plus 10 (10+x^2) polynomial
		BigInteger[] sq_p10 = {BigInteger.TEN, BigInteger.ZERO, BigInteger.ONE};
		
		try {
			square = new EncryptedPolynomial(sq, pub);
			identity = new EncryptedPolynomial(id, pub);
			square_plus10 = new EncryptedPolynomial(sq_p10, pub);
		} catch (BigIntegerClassNotValid e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testEvaluate() throws PublicKeysNotEqualException, BigIntegerClassNotValid {
		EncryptedInteger ans = identity.evaluate(BigInteger.TEN);
		assertEquals(BigInteger.TEN, ans.decrypt(priv));
		
		ans = square.evaluate(BigInteger.TEN);
		assertEquals(BigInteger.TEN.pow(2), ans.decrypt(priv));
		
		BigInteger twelve = new BigInteger("12");
		BigInteger expected = twelve.pow(2).add(BigInteger.TEN);
		ans = square_plus10.evaluate(twelve);
		assertEquals(expected, ans.decrypt(priv));
	}
	
	public void testAdd() throws PublicKeysNotEqualException, SizesNotEqualException, BigIntegerClassNotValid {
		// Try sum of identity and square
		EncryptedPolynomial ans = identity.add(square);	// should yield f(x) = x + x^2 [0, 1, 1]
		BigInteger[] expected1 = {BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE};
		
		// Decrypt coefficients and make sure they are what was expected
		for (int i=0; i < expected1.length; i++) {
			assertEquals(expected1[i], ans.getCoefficients()[i].decrypt(priv));
		}
		
		// Try sum of identity and square and square_plus10
		ans = ans.add(square_plus10); // should yield f(x) = 10 + x + 2x^2 [10, 1, 2]
		BigInteger[] expected2 = {BigInteger.TEN, BigInteger.ONE, new BigInteger("2")};
		
		// Decrypt coefficients and make sure they are what was expected
		for (int i=0; i < expected2.length; i++) {
			assertEquals(expected2[i], ans.getCoefficients()[i].decrypt(priv));
		}
	}
	
	public void testMultiplyConstant() throws PublicKeysNotEqualException, SizesNotEqualException, BigIntegerClassNotValid {
		// Try multiply of identity by 10
		EncryptedPolynomial ans = identity.multiply(BigInteger.TEN);
		BigInteger[] expected1 = {BigInteger.ZERO, BigInteger.TEN, BigInteger.ZERO};
		
		// Decrypt coefficients and make sure they are what was expected
		for (int i=0; i < expected1.length; i++) {
			assertEquals(expected1[i], ans.getCoefficients()[i].decrypt(priv));
		}
		
		// Try multiply of square plus 10 by 2
		ans = square_plus10.multiply(new BigInteger("2")); // should yield f(x) = 20 + 2x^2 [20, 0, 2]
		BigInteger[] expected2 = {new BigInteger("20"), BigInteger.ZERO, new BigInteger("2")};
		
		// Decrypt coefficients and make sure they are what was expected
		for (int i=0; i < expected2.length; i++) {
			assertEquals(expected2[i], ans.getCoefficients()[i].decrypt(priv));
		}
	}
	
	public void testMultiplyKnownPoly() throws SizesNotEqualException, PublicKeysNotEqualException, BigIntegerClassNotValid {
		// multiply identity by identity
		BigInteger[] identity_plain = {BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO};
		EncryptedPolynomial ans = identity.multiply(identity_plain);
		BigInteger[] expected1 = {BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO};
		
		// Decrypt coefficients and make sure they are what was expected
		for (int i=0; i < expected1.length; i++) {
			assertEquals(expected1[i], ans.getCoefficients()[i].decrypt(priv));
		}
		
		// Try a fully populated trial
		BigInteger[] test2 = {BigInteger.ONE, BigInteger.ONE, BigInteger.ONE};
		EncryptedPolynomial poly2 = new EncryptedPolynomial(test2, pub);
		BigInteger[] expected2 = {BigInteger.ONE, new BigInteger("2"), new BigInteger("3"), new BigInteger("2"), BigInteger.ONE}; 
		
		ans = poly2.multiply(test2);
		
		// Decrypt coefficients and make sure they are what was expected
		for (int i=0; i < expected2.length; i++) {
			assertEquals(expected2[i], ans.getCoefficients()[i].decrypt(priv));
		}
	}
	
	public void testSerialization() throws IOException, ClassNotFoundException, BigIntegerClassNotValid {
		// Save the integer to an output stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(square_plus10);
		
		// Load a new object from the output stream
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		EncryptedPolynomial tmp = (EncryptedPolynomial)ois.readObject();
		
		BigInteger[] expected = {BigInteger.TEN, BigInteger.ZERO, BigInteger.ONE};
		
		for (int i=0; i < expected.length; i++) {
			assertEquals(expected[i], tmp.getCoefficients()[i].decrypt(priv));
		}
	}
}
