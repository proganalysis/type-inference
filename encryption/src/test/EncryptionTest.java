package test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPrivateKey;

import encryption.*;
import junit.framework.TestCase;

public class EncryptionTest extends TestCase {

	private static final int pti1 = 2, pti2 = 2, pti3 = 1000, pti4 = -1;
	private byte[] cti1, cti2, cti3, cti4, cts1, cts2, cts3;
	private static final String pts1 = "Hello", pts2 = "Hello", pts3 = "Jello";
	
	public void testAH() {
		AHEncryptor ah = new AHEncryptor(".");
		System.out.println(pti1 + ": " + ah.encrypt(pti1));
		System.out.println(pti1 + ": " + ah.encrypt(pti1));
	}
	
	public void testDoubleAH() {
		int modulusLength = 64;
		PaillierPrivateKey privateKey = PaillierPrivateKey.create(modulusLength);
	    PaillierContext context = new PaillierContext(privateKey.getPublicKey(),
	        true, modulusLength);
	    float a = 2.5f, b = 3.6f;
	    EncryptedNumber cipherTextA = context.encrypt(a);
	    EncryptedNumber cipherTextB = context.encrypt(b);
	    EncryptedNumber encryptedResult = context.add(cipherTextA, cipherTextB);
	    double result = encryptedResult.decrypt(privateKey).decodeDouble();
	    System.out.println(cipherTextA.calculateCiphertext() + ", " + cipherTextB.calculateCiphertext()
	    + ", " + result + ", " + (float) result);
	    BigInteger cipherA = cipherTextA.calculateCiphertext();
	    int exp = cipherTextA.getExponent();
	    context = new PaillierContext(privateKey.getPublicKey(), true, modulusLength);
	    EncryptedNumber enA = new EncryptedNumber(context, cipherA, exp);
	    System.out.println(cipherA + ", " + exp + ", " + enA.decrypt(privateKey).decodeDouble());
	}
	
//	public void testDET() {
//		Encryption e;
//		e = new Deterministic();
//		cti1 = e.encrypt(pti1);
//		e = new Deterministic();
//		cti2 = e.encrypt(pti2);
//		cti3 = e.encrypt(pti3);
//		assertTrue(pti3 == (int) e.decrypt(cti3));
//		cts1 = e.encrypt(pts1);
//		cts2 = e.encrypt(pts2);
//		assertTrue(Arrays.equals((byte[]) cts1, (byte[]) cts2));
//		try {
//			System.out.println(new String((byte[]) cts1, "UTF-16"));
//			System.out.println(new String((byte[]) cts2, "UTF-16"));
//			System.out.println(e.decrypt(new String((byte[]) cts1, "ISO-8859-1").getBytes("ISO-8859-1")));
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
//		e = new Deterministic();
//		assertTrue(pti1 == (int) e.decrypt(cti1));
//		assertTrue(pts1.equals(e.decrypt(cts1)));
//		assertTrue(Computation.equals(cti1, cti2));
//		assertFalse(Computation.equals(cti1, cti3));
//		e = new Deterministic();
//		assertTrue(Computation.equals(cts1, cts2));
//		cts3 = e.encrypt(pts3);
//		cti4 = e.encrypt(pti4);
//		assertFalse(Computation.equals(cts1, cts3));
//		// 1 * 1000
//		assertEquals(pti1*pti3, e.decrypt(Computation.multiply(cti1, cti3)));
//		// -1 * 1000
//		assertEquals(pti4*pti3, e.decrypt(Computation.multiply(cti4, cti3)));
//		// -1 * (-1)
//		assertEquals(pti4*pti4, e.decrypt(Computation.multiply(cti4, cti4)));
//	}
	
//	public void testAH() {
//		Encryption e;
//		e = new Homomorphic();
//		cti1 = e.encrypt(pti1);
//		cti4 = e.encrypt(pti4);
//		e = new Homomorphic();
//		assertTrue(pti1 == (int) e.decrypt(cti1));
//		assertTrue(pti4 == (int) e.decrypt(cti4));
//		cti2 = e.encrypt(pti2);
//		cti3 = e.encrypt(pti3);
//		System.out.println(pti1 + ": " + cti1 + ", " + cti1.toString().length());
//		System.out.println(pti2 + ": " + cti2 + ", " + cti2.toString().length());
//		System.out.println(pti3 + ": " + cti3 + ", " + cti3.toString().length());
//		System.out.println(pti4 + ": " + cti4 + ", " + cti4.toString().length());
//		// 1 + 1000
//		assertEquals(pti1 + pti3, e.decrypt(Computation.add(cti1, cti3)));
//		// 1 + (-1)
//		assertEquals(pti1 + pti4, e.decrypt(Computation.add(cti1, cti4)));
//		// 1000 + (-1)
//		assertEquals(pti3 + pti4, e.decrypt(Computation.add(cti3, cti4)));
//		// 1 - (-1)
//		assertEquals(pti1 - pti4, e.decrypt(Computation.minus(cti1, cti4)));
//		// 1000 - 1
//		assertEquals(pti3 - pti1, e.decrypt(Computation.minus(cti3, cti1)));
//		// 1 - 1000
//		assertEquals(pti1 - pti3, e.decrypt(Computation.minus(cti1, cti3)));
//		// 1 * 1000
//		assertEquals(pti1 * pti3, e.decrypt(Computation.multiply(cti1, pti3)));
//		// 1 * (-1)
//		assertEquals(pti1 * pti4, e.decrypt(Computation.multiply(cti1, pti4)));
//	}
	
//	public void testOPE() {
//		Encryption e;
//		e = new OrderPreserving();
//		cti1 = e.encrypt(pti1);
//		cti2 = e.encrypt(pti2);
//		cts1 = e.encrypt(pts1);
//		e = new OrderPreserving();
//		cti3 = e.encrypt(pti3);
//		assertFalse(Computation.lessThan(cti1, cti2));
//		assertTrue(pti1 == (int) e.decrypt(cti1));
//		assertTrue(pts1.equals(e.decrypt(cts1)));
//		cts3 = e.encrypt(pts3);
//		cti4 = e.encrypt(pti4);
//		assertTrue(Computation.greaterThan(cti3, cti4));
//		assertFalse(Computation.lessThan(cti3, cti1));
//		assertFalse(Computation.greaterThan(cti1, cti3));
//		assertTrue(Computation.compareTo(cts1, cts3) < 0);
//		assertTrue("h".equals(e.decrypt(e.encrypt("h"))));
//		assertTrue(Computation.compareTo(e.encrypt("h"), e.encrypt("he")) < 0);
//	}
	
	public void testDET() {
		DETEncryptorOld e = new DETEncryptorOld();
		cti1 = e.encrypt(ByteBuffer.allocate(4).putInt(pti1).array());
		cts1 = e.encrypt(pts1.getBytes());
		System.out.println(pti1 + ": " + bytesToHex(cti1));
		System.out.println(pts1 + ": " + bytesToHex(cts1));
	}
	
	public void testRND() {
		Random e;
		e = new Random();
		cti1 = e.encrypt(ByteBuffer.allocate(4).putInt(pti1).array());
		cts1 = e.encrypt(pts1.getBytes());
		System.out.println(pti1 + ": " + bytesToHex(cti1));
		System.out.println(pts1 + ": " + bytesToHex(cts1));
		System.out.println(pti1 + ":: " + ByteBuffer.wrap(e.decrypt(cti1)).getInt());
		System.out.println(pts1 + ":: " + new String(e.decrypt(cts1)));
	}
	
	public String bytesToHex(byte[] in) {
		final StringBuilder builder = new StringBuilder();
	    for(byte b : in) {
	        builder.append(String.format("%02x", b));
	    }
	    return builder.toString();
	}
	
	public void testRegex() {
		String s = "abg\t14f";
		System.out.println(s);
		assertTrue(s.matches(".*\t\\d+$"));
	}

}
