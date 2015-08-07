package encryption;

import java.math.BigInteger;
import java.util.Arrays;

import thep.paillier.EncryptedInteger;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;

public class Computation {

	public static boolean equals(byte[] b1, byte[] b2) {
		return Arrays.equals(b1, b2);
	}

	public static boolean equals(byte[][] b1, byte[][] b2) {
		return Arrays.deepEquals(b1, b2);
	}
	
	public static boolean notEquals(byte[] b1, byte[] b2) {
		return !equals(b1, b2);
	}
	
	public static boolean notEquals(byte[][] b1, byte[][] b2) {
		return !equals(b1, b2);
	}

	public static boolean lessThan(byte[] b1, byte[] b2) {
		byte[] b1det = Conversion.convert(b1, "OPE", "DET");
		byte[] b2det = Conversion.convert(b2, "OPE", "DET");
		return compareTo(b1, b2) == -1 && !equals(b1det, b2det);
	}
	
	public static boolean greaterThan(byte[] b1, byte[] b2) {
		byte[] b1det = Conversion.convert(b1, "OPE", "DET");
		byte[] b2det = Conversion.convert(b2, "OPE", "DET");
		return compareTo(b1, b2) == 1 && !equals(b1det, b2det);
	}
	
	public static boolean lessThanOrEqualTo(byte[] b1, byte[] b2) {
		return !greaterThan(b1, b2);
	}
	
	public static boolean greaterThanOrEqualTo(byte[] b1, byte[] b2) {
		return !lessThan(b1, b2);
	}
	
	public static boolean lessThan(byte[][] b1, byte[][] b2) {
		byte[][] b1det = Conversion.convert(b1, "OPE", "DET");
		byte[][] b2det = Conversion.convert(b2, "OPE", "DET");
		return compareTo(b1, b2) == -1 && !equals(b1det, b2det);
	}
	
	public static boolean greaterThan(byte[][] b1, byte[][] b2) {
		byte[][] b1det = Conversion.convert(b1, "OPE", "DET");
		byte[][] b2det = Conversion.convert(b2, "OPE", "DET");
		return compareTo(b1, b2) == 1 && !equals(b1det, b2det);
	}
	
	public static boolean lessThanOrEqualTo(byte[][] b1, byte[][] b2) {
		return !greaterThan(b1, b2);
	}
	
	public static boolean greaterThanOrEqualTo(byte[][] b1, byte[][] b2) {
		return !lessThan(b1, b2);
	}
	
	private static int compareTo(byte[] ba1, byte[] ba2) {
		if (ba1.length < ba2.length) return -1;
		if (ba1.length > ba2.length) return 1;
		for (int i = 0; i < ba1.length; i++) {
			if (ba1[i] < ba2[i])
				return -1;
			if (ba1[i] > ba2[i])
				return 1;
		}
		return 0;
	}

	private static int compareTo(byte[][] b1, byte[][] b2) {
		if (b1.length < b2.length) return -1;
		if (b1.length > b2.length) return 1;
		for (int i = 0; i < b1.length; i++) {
			int compareResult = compareTo(b1[i], b2[i]);
			if (compareResult > 0)
				return 1;
			if (compareResult < 0)
				return -1;
		}
		return 0;
	}
	
	public static byte[] divide(byte[] b1, byte[] b2) {
		return divide(b1, b2, false);
	}
	
	private static byte[] divide(byte[] b1, byte[] b2, boolean mod) {
		byte[] diffAH = b1;
		byte[] diffOPE = Conversion.convert(b1, "AH", "OPE");
		byte[] b2OPE = Conversion.convert(b2, "AH", "OPE");
		int i = 0;
		while (greaterThanOrEqualTo(diffOPE, b2OPE)) {
			diffAH = minus(diffAH, b2);
			diffOPE = Conversion.convert(diffAH, "AH", "OPE");
			i++;
		}
		if (mod) return diffAH;
		return Conversion.encrypt(i, "AH");
	}
	
	public static byte[] mod(byte[] b1, byte[] b2) {
		return divide(b1, b2, true);
	}
	
	public static byte[] multiply(byte[] b1, byte[] b2) {
		BigInteger clearb2 = BigInteger.valueOf(Conversion.decrypt(b2, "AH"));
		EncryptedInteger e1 = Homomorphic.ei;
		Homomorphic.ei.setCipherVal(new BigInteger(b1));
		EncryptedInteger e2 = new EncryptedInteger(e1);
		e2.setCipherVal(new BigInteger(b2));
		try {
			e1 = e1.multiply(clearb2);
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
		return e1.getCipherVal().toByteArray();
	}
	
	public static byte[] add(byte[] b1, byte[] b2) {
		EncryptedInteger e1 = Homomorphic.ei;
		Homomorphic.ei.setCipherVal(new BigInteger(b1));
		EncryptedInteger e2 = new EncryptedInteger(e1);
		e2.setCipherVal(new BigInteger(b2));
		try {
			e1 = e1.add(e2);
		} catch (PublicKeysNotEqualException e) {
			e.printStackTrace();
		}
		return e1.getCipherVal().toByteArray();
	}
	
	public static byte[] minus(byte[] b1, byte[] b2) {
		EncryptedInteger e1 = Homomorphic.ei;
		Homomorphic.ei.setCipherVal(new BigInteger(b1));
		EncryptedInteger e2 = new EncryptedInteger(e1);
		e2.setCipherVal(new BigInteger(b2));
		try {
			e2 = e2.multiply(new BigInteger("-1"));
			e1 = e1.add(e2);
		} catch (PublicKeysNotEqualException e) {
			e.printStackTrace();
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
		return e1.getCipherVal().toByteArray();
	}
	
	public static byte[][] add(byte[][] a, byte[][] b) {
		int aLen = a.length;
		int bLen = b.length;
		byte[][] c = new byte[aLen + bLen][];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

}
