package encryption;

import encryption.encryptedValue.EncryptedValue;

public class Conversion {

	public static EncryptedValue encrypt(Object ptext, String to) {
		Encryption toType = createEncryption(to);
		return toType.encrypt(ptext);
	}

	private static Object decrypt(EncryptedValue ctext, String from) {
		Encryption fromType = createEncryption(from);
		return fromType.decrypt(ctext);
	}

	public static EncryptedValue convert(EncryptedValue ctext, String from, String to) {
		Object ptext = decrypt(ctext, from);
		return encrypt(ptext, to);
	}

	private static Encryption createEncryption(String type) {
		switch (type) {
		case "RND":
			return new Random();
		case "OPE":
			return new OrderPreserving();
		case "AH":
			return new Homomorphic();
		default:
			assert (type.equals("DET"));
			return new Deterministic();
		}
	}

}
