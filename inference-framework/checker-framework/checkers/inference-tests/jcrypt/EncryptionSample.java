import encryption.Computation;
import encryption.Conversion;

class EncryptionSample {
	public static void main(String[] args) {
		byte[] b1 = new byte[4];
		byte[] b2 = b1;
		String s = "";
		Computation.add(b1, b2);
		Computation.lessThan(b1, b2);
		Computation.greaterThan(b1, b2);
		Computation.lessThanOrEqualTo(b1, b2);
		Computation.greaterThanOrEqualTo(b1, b2);
		Computation.notEquals(b1, b2);
		Computation.equals(b1, b2);
		Conversion.encrypt(0, s);
		Conversion.decrypt(b1, s);
		Conversion.convert(b1, s, s);
	}
}
