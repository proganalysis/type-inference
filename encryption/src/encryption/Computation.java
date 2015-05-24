package encryption;

import java.math.BigInteger;
import java.util.Arrays;

import thep.paillier.EncryptedInteger;
import thep.paillier.exceptions.PublicKeysNotEqualException;

public class Computation {

	public static boolean equals(byte[] b1, byte[] b2) {
		return Arrays.equals(b1, b2);
	}

	public static boolean equals(byte[][] b1, byte[][] b2) {
		return Arrays.deepEquals(b1, b2);
	}

	public static int compareTo(byte[] ba1, byte[] ba2) {
		for (int i = 0; i < ba1.length; i++) {
			if (ba1[i] < ba2[i])
				return -1;
			if (ba1[i] > ba2[i])
				return 1;
		}
		return 0;
	}

	public static int compareTo(byte[][] b1, byte[][] b2) {
		for (int i = 0; i < b1.length; i++) {
			int compareResult = compareTo(b1[i], b2[i]);
			if (compareResult > 0)
				return 1;
			if (compareResult < 0)
				return -1;
		}
		return 0;
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

}
