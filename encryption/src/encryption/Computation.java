package encryption;

import java.math.BigInteger;
import java.util.Arrays;

import thep.paillier.EncryptedInteger;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;

public class Computation {
	
	public static boolean equals(Object b1, Object b2) {
		boolean res;
		if (b1 instanceof BigInteger) { // int
			res = b1.equals(b2);
		} else { // String
			res = Arrays.equals((byte[]) b1, (byte[]) b2);
		}
		return res;
	}

	public static boolean notEquals(Object b1, Object b2) {
		return !equals(b1, b2);
	}
	
	public static int compareTo(Object b1, Object b2) {
		// only for String
		String[] s1array = b1.toString().split(" ");
		String[] s2array = b2.toString().split(" ");
		int len1 = s1array.length;
		int len2 = s2array.length;
		int n = Math.min(len1, len2) - 1;
		int k = 0;
		while (k < n) {
			String s1 = s1array[k];
			String s2 = s2array[k];
			if (!s1.equals(s2)) {
				return new BigInteger(s1).compareTo(new BigInteger(s2));
			}
			k++;
		}
		return len1 - len2;
	}

	public static boolean lessThan(Object b1, Object b2) {
		// only for int
		return new BigInteger(b1.toString()).compareTo(new BigInteger(b2.toString())) == -1;
	}

	public static boolean greaterThan(Object b1, Object b2) {
		// only for int
		return new BigInteger(b1.toString()).compareTo(new BigInteger(b2.toString())) == 1;
	}

	public static boolean lessThanOrEqualTo(Object b1, Object b2) {
		// only for int
		return new BigInteger(b1.toString()).compareTo(new BigInteger(b2.toString())) < 1;
	}

	public static boolean greaterThanOrEqualTo(Object b1, Object b2) {
		// only for int
		return new BigInteger(b1.toString()).compareTo(new BigInteger(b2.toString())) > -1;
	}

//	public static Object divide(Object b1, Object b2) {
//		int c1 = (int) Conversion.decrypt(b1, b1.getEncryptKind());
//		int c2 = (int) Conversion.decrypt(b2, b2.getEncryptKind());
//		return Conversion.encrypt(c1 / c2, b1.getEncryptKind());
//	}
//
//	public static Object mod(Object b1, Object b2) {
//		int c1 = (int) Conversion.decrypt(b1, b1.getEncryptKind());
//		int c2 = (int) Conversion.decrypt(b2, b2.getEncryptKind());
//		return Conversion.encrypt(c1 % c2, b1.getEncryptKind());
//	}
//
//	public static Object shiftLeft(Object b1, Object b2) {
//		int c1 = (int) Conversion.decrypt(b1, b1.getEncryptKind());
//		int c2 = (int) Conversion.decrypt(b2, b2.getEncryptKind());
//		return Conversion.encrypt(c1 << c2, b1.getEncryptKind());
//	}

	public static Object shiftLeft(Object b1, int c2) {
		return multiply(b1, (int) Math.pow(2, c2));
	}

//	public static Object shiftRight(Object b1, Object b2) {
//		int c1 = (int) Conversion.decrypt(b1, b1.getEncryptKind());
//		int c2 = (int) Conversion.decrypt(b2, b2.getEncryptKind());
//		return Conversion.encrypt(c1 >> c2, b1.getEncryptKind());
//	}

	public static Object multiply(Object b1, Object b2) {
		return ((BigInteger) b1).multiply((BigInteger) b2);
	}

	public static Object multiply(Object b1, int b2) {
		EncryptedInteger e1 = Homomorphic.ei;
		Homomorphic.ei.setCipherVal((BigInteger) b1);
		try {
			e1 = e1.multiply(BigInteger.valueOf(b2));
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
		return e1.getCipherVal();
	}

	public static Object add(Object b1, Object b2) {
		EncryptedInteger e1 = Homomorphic.ei;
		Homomorphic.ei.setCipherVal((BigInteger) b1);
		EncryptedInteger e2 = new EncryptedInteger(e1);
		e2.setCipherVal((BigInteger) b2);
		try {
			e1 = e1.add(e2);
		} catch (PublicKeysNotEqualException e) {
			e.printStackTrace();
		}
		return e1.getCipherVal();
	}

	public static Object minus(Object b1, Object b2) {
		EncryptedInteger e1 = Homomorphic.ei;
		Homomorphic.ei.setCipherVal((BigInteger) b1);
		EncryptedInteger e2 = new EncryptedInteger(e1);
		e2.setCipherVal((BigInteger) b2);
		try {
			e2 = e2.multiply(new BigInteger("-1"));
			e1 = e1.add(e2);
		} catch (PublicKeysNotEqualException e) {
			e.printStackTrace();
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
		return e1.getCipherVal();
	}

}
