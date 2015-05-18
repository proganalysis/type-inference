package encryption;

import encryption.encryptedValue.EncryptedValue;

public class Conversion {
	
	public static Encryption rnd = new Random();
	public static Encryption ope = new OrderPreserving();
	public static Encryption ah = new Homomorphic();
	public static Encryption det = new Deterministic();
	

	public static EncryptedValue encrypt(Object ptext, String to) {
		Encryption toType = createEncryption(to);
		return toType.encrypt(ptext);
	}

	public static Object decrypt(EncryptedValue ctext, String from) {
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
			return rnd;
		case "OPE":
			return ope;
		case "AH":
			return ah;
		default:
			assert (type.equals("DET"));
			return det;
		}
	}

}
