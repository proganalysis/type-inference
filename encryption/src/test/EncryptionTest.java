package test;

import encryption.*;
import encryption.encryptedValue.EncryptedValue;
import junit.framework.TestCase;

public class EncryptionTest extends TestCase {

	private static final int pti1 = 1234;
	private static final int pti2 = 1234;
	private static final int pti3 = 1235;
	private EncryptedValue cti1, cti2, cti3, cts1, cts2, cts3;
	private static final String pts1 = "Hello";
	private static final String pts2 = "Hello";
	private static final String pts3 = "Jello";
	private Encryption e;

	public void testDET() {
		e = new Deterministic();
		cti1 = e.encrypt(pti1);
		cts1 = e.encrypt(pts1);
		e = new Deterministic();
		assertTrue(pti1 == (int) e.decrypt(cti1));
		assertTrue(pts1.equals(e.decrypt(cts1)));
		cti2 = e.encrypt(pti2);
		assertTrue(cti1.equals(cti2));
		cti3 = e.encrypt(pti3);
		assertFalse(cti1.equals(cti3));
		e = new Deterministic();
		cts2 = e.encrypt(pts2);
		assertTrue(cts1.equals(cts2));
		cts3 = e.encrypt(pts3);
		assertFalse(cts1.equals(cts3));
	}
	
	public void testAH() {
		e = new Homomorphic();
		cti1 = e.encrypt(pti1);
		e = new Homomorphic();
		assertTrue(pti1 == (int) e.decrypt(cti1));
		cti2 = e.encrypt(pti2);
		cti3 = e.encrypt(pti3);
		// test addition
		assertEquals(pti1 + pti3, e.decrypt(cti1.add(cti3)));
		// test subtraction
//		assertEquals(pti3 - pti1, (int) e.decrypt(cti3.subtract(cti1)));
//		assertEquals(pti1 - pti3, - (int) e.decrypt(cti3.subtract(cti1)));
//		assertEquals(pti2 - pti1, e.decrypt(cti2.subtract(cti1)));
	}
	
	public void testOPE() {
		e = new OrderPreserving();
		cti1 = e.encrypt(pti1);
		cts1 = e.encrypt(pts1);
		e = new OrderPreserving();
		assertTrue(pti1 == (int) e.decrypt(cti1));
		assertTrue(pts1.equals(e.decrypt(cts1)));
		cts3 = e.encrypt(pts3);
		cti3 = e.encrypt(pti3);
		assertTrue(cti3.largerThan(cti1));
		assertTrue(cts3.largerThan(cts1));
	}
	
	public void testRND() {
		e = new Random();
		cti1 = e.encrypt(pti1);
		cts1 = e.encrypt(pts1);
		e = new Random();
		assertTrue(pti1 == (int) e.decrypt(cti1));
		assertTrue(pts1.equals(e.decrypt(cts1)));
	}

}
