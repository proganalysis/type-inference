package test;

import encryption.*;
import junit.framework.TestCase;

public class EncryptionTest extends TestCase {

	private static final int pti1 = 11234;
	private static final int pti2 = 11234;
	private static final int pti3 = 11235;
	private byte[] cti1, cti2, cti3;
	private byte[][] cts1, cts2, cts3;
	private static final String pts1 = "Hello";
	private static final String pts2 = "Hello";
	private static final String pts3 = "Jello";
	
	public void testDET() {
		Encryption e;
		e = new Deterministic();
		cti1 = e.encrypt(pti1);
		cts1 = e.encrypt(pts1);
		e = new Deterministic();
		assertTrue(pti1 == (int) e.decrypt(cti1));
		assertTrue(pts1.equals(e.decrypt(cts1)));
		cti2 = e.encrypt(pti2);
		assertTrue(Computation.equals(cti1, cti2));
		cti3 = e.encrypt(pti3);
		assertFalse(Computation.equals(cti1, cti3));
		e = new Deterministic();
		cts2 = e.encrypt(pts2);
		assertTrue(Computation.equals(cts1, cts2));
		cts3 = e.encrypt(pts3);
		assertFalse(Computation.equals(cts1, cts3));
	}
	
	public void testAH() {
		Encryption e;
		e = new Homomorphic();
		cti1 = e.encrypt(pti1);
		e = new Homomorphic();
		assertTrue(pti1 == (int) e.decrypt(cti1));
		cti2 = e.encrypt(pti2);
		cti3 = e.encrypt(pti3);
		assertEquals(pti1 + pti3, e.decrypt(Computation.add(cti1, cti3)));
	}
	
	public void testOPE() {
		Encryption e;
		e = new OrderPreserving();
		cti1 = e.encrypt(pti1);
		cts1 = e.encrypt(pts1);
		e = new OrderPreserving();
		assertTrue(pti1 == (int) e.decrypt(cti1));
		assertTrue(pts1.equals(e.decrypt(cts1)));
		cts3 = e.encrypt(pts3);
		cti3 = e.encrypt(pti3);
		assertEquals(1, Computation.compareTo(cti3, cti1));
		assertEquals(1, Computation.compareTo(cts3, cts1));
	}
	
	public void testRND() {
		Encryption e;
		e = new Random();
		cti1 = e.encrypt(pti1);
		cts1 = e.encrypt(pts1);
		e = new Random();
		assertTrue(pti1 == (int) e.decrypt(cti1));
		assertTrue(pts1.equals(e.decrypt(cts1)));
	}

}
