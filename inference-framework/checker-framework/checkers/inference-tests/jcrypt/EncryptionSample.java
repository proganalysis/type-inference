import encryption.Computation;
import encryption.Conversion;
import encryption.EncryptedData;

class EncryptionSample {
	public static void main(String[] args) {
		Object b1 = null, b2 = null;
		String s = null;
		Computation.add(b1, b1);
		Computation.minus(b1, b1);
		Computation.multiply(b1, b1);
//		Computation.divide(b1, b1);
//		Computation.mod(b1, b1);
//		Computation.shiftLeft(b1, b1);
//		Computation.shiftRight(b1, b1);
		Computation.lessThan(b1, b1);
		Computation.greaterThan(b1, b1);
		Computation.lessThanOrEqualTo(b1, b1);
		Computation.greaterThanOrEqualTo(b1, b1);
		Computation.notEquals(b1, b1);
		Computation.equals(b1, b1);
		Conversion.encrypt(0, s);
		Conversion.encryptSpe(0, s);
		Conversion.decrypt(b1, s);
		Conversion.convert(b1, s, s);
		Conversion.convertSpe(b1, s, s);
	}
}
