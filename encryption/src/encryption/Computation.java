package encryption;

import java.math.BigInteger;
import encryption.EncryptedData.DataKind;
import encryption.EncryptedData.EncryptKind;
import thep.paillier.EncryptedInteger;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;

public class Computation {

	public static boolean equals(EncryptedData b1, EncryptedData b2) {
		return b1.getValue().equals(b2.getValue());
	}

	public static boolean notEquals(EncryptedData b1, EncryptedData b2) {
		return !b1.getValue().equals(b2.getValue());
	}

	public static boolean lessThan(EncryptedData b1, EncryptedData b2) {
		return b1.getValue().compareTo(b2.getValue()) == -1;
	}

	public static boolean greaterThan(EncryptedData b1, EncryptedData b2) {
		return b1.getValue().compareTo(b2.getValue()) == 1;
	}

	public static boolean lessThanOrEqualTo(EncryptedData b1, EncryptedData b2) {
		return b1.getValue().compareTo(b2.getValue()) < 1;
	}

	public static boolean greaterThanOrEqualTo(EncryptedData b1, EncryptedData b2) {
		return b1.getValue().compareTo(b2.getValue()) > -1;
	}

	public static EncryptedData divide(EncryptedData b1, EncryptedData b2) {
		int c1 = (int) Conversion.decrypt(b1, b1.getEncryptKind());
		int c2 = (int) Conversion.decrypt(b2, b2.getEncryptKind());
		return Conversion.encrypt(c1 / c2, b1.getEncryptKind());
	}

	public static EncryptedData mod(EncryptedData b1, EncryptedData b2) {
		int c1 = (int) Conversion.decrypt(b1, b1.getEncryptKind());
		int c2 = (int) Conversion.decrypt(b2, b2.getEncryptKind());
		return Conversion.encrypt(c1 % c2, b1.getEncryptKind());
	}

	public static EncryptedData shiftLeft(EncryptedData b1, EncryptedData b2) {
		int c1 = (int) Conversion.decrypt(b1, b1.getEncryptKind());
		int c2 = (int) Conversion.decrypt(b2, b2.getEncryptKind());
		return Conversion.encrypt(c1 << c2, b1.getEncryptKind());
	}

	public static EncryptedData shiftLeft(EncryptedData b1, int c2) {
		return multiply(b1, (int) Math.pow(2, c2));
	}

	public static EncryptedData shiftRight(EncryptedData b1, EncryptedData b2) {
		int c1 = (int) Conversion.decrypt(b1, b1.getEncryptKind());
		int c2 = (int) Conversion.decrypt(b2, b2.getEncryptKind());
		return Conversion.encrypt(c1 >> c2, b1.getEncryptKind());
	}

	public static EncryptedData multiply(EncryptedData b1, EncryptedData b2) {
		return new EncryptedData(DataKind.INT, EncryptKind.DET, b1.getValue().multiply(b2.getValue()));
	}

	public static EncryptedData multiply(EncryptedData b1, int b2) {
		EncryptedInteger e1 = Homomorphic.ei;
		Homomorphic.ei.setCipherVal(b1.getValue());
		try {
			e1 = e1.multiply(BigInteger.valueOf(b2));
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
		return new EncryptedData(DataKind.INT, EncryptKind.AH, e1.getCipherVal());
	}

	public static EncryptedData add(EncryptedData b1, EncryptedData b2) {
		if (b1.getDataKind() == DataKind.INT) {
			EncryptedInteger e1 = Homomorphic.ei;
			Homomorphic.ei.setCipherVal(b1.getValue());
			EncryptedInteger e2 = new EncryptedInteger(e1);
			e2.setCipherVal(b2.getValue());
			try {
				e1 = e1.add(e2);
			} catch (PublicKeysNotEqualException e) {
				e.printStackTrace();
			}
			return new EncryptedData(DataKind.INT, EncryptKind.AH, e1.getCipherVal());
		} else {
			BigInteger res = new BigInteger(b1.getValue().toString() + b2.getValue().toString());
			return new EncryptedData(DataKind.STRING, b1.getEncryptKind(), res);
		}
	}

	public static EncryptedData minus(EncryptedData b1, EncryptedData b2) {
		EncryptedInteger e1 = Homomorphic.ei;
		Homomorphic.ei.setCipherVal(b1.getValue());
		EncryptedInteger e2 = new EncryptedInteger(e1);
		e2.setCipherVal(b2.getValue());
		try {
			e2 = e2.multiply(new BigInteger("-1"));
			e1 = e1.add(e2);
		} catch (PublicKeysNotEqualException e) {
			e.printStackTrace();
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
		return new EncryptedData(DataKind.INT, EncryptKind.AH, e1.getCipherVal());
	}

}
