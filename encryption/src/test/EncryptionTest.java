package test;

import encryption.*;
import junit.framework.TestCase;

public class EncryptionTest extends TestCase {

	private static final int pti1 = 1, pti2 = 1, pti3 = 1000, pti4 = -1;
	private byte[] cti1, cti2, cti3, cti4;
	private byte[][] cts1, cts2, cts3;
	private static final String pts1 = "Hello", pts2 = "Hello", pts3 = "Jello";
	
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
		cti4 = e.encrypt(pti4);
		e = new Homomorphic();
		assertTrue(pti1 == (int) e.decrypt(cti1));
		assertTrue(pti4 == (int) e.decrypt(cti4));
		cti2 = e.encrypt(pti2);
		cti3 = e.encrypt(pti3);
		// 1 + 1000
		assertEquals(pti1 + pti3, e.decrypt(Computation.add(cti1, cti3)));
		// 1 + (-1)
		assertEquals(pti1 + pti4, e.decrypt(Computation.add(cti1, cti4)));
		// 1000 + (-1)
		assertEquals(pti3 + pti4, e.decrypt(Computation.add(cti3, cti4)));
		// 1 - (-1)
		assertEquals(pti1 - pti4, e.decrypt(Computation.minus(cti1, cti4)));
		// 1000 - 1
		assertEquals(pti3 - pti1, e.decrypt(Computation.minus(cti3, cti1)));
		// 1 - 1000
		assertEquals(pti1 - pti3, e.decrypt(Computation.minus(cti1, cti3)));
		// 1 * 1000
		assertEquals(pti1 * pti3, e.decrypt(Computation.multiply(cti1, cti3)));
		// 1 * (-1)
		assertEquals(pti1 * pti4, e.decrypt(Computation.multiply(cti1, cti4)));
		// 1 / 1
		assertEquals(pti1 / pti2, e.decrypt(Computation.divide(cti1, cti2)));
	}
	
	public void testOPE() {
		Encryption e;
		e = new OrderPreserving();
		cti1 = e.encrypt(pti1);
		cts1 = e.encrypt(pts1);
		e = new OrderPreserving();
		cti2 = e.encrypt(pti2);
		assertFalse(Computation.lessThan(cti1, cti2));
		assertTrue(pti1 == (int) e.decrypt(cti1));
		assertTrue(pts1.equals(e.decrypt(cts1)));
		cts3 = e.encrypt(pts3);
		cti3 = e.encrypt(pti3);
		assertTrue(Computation.greaterThan(cti3, cti1));
		assertTrue(Computation.lessThan(cti1, cti3));
		assertTrue(Computation.lessThan(cts1, cts3));
	}
	
	public void testRND() {
		Encryption e;
		e = new Random();
		cti1 = e.encrypt(pti1);
		cts1 = e.encrypt(pts1);
		cts2 = e.encrypt(pts2);
		e = new Random();
		assertTrue(pti1 == (int) e.decrypt(cti1));
		assertTrue(pts1.equals(e.decrypt(cts1)));
		assertEquals(pts1 + pts2, e.decrypt(Computation.add(cts1, cts2)));
	}

}
