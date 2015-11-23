package encryption;

import encryption.EncryptedData.EncryptKind;

public class Conversion {
	
	public static Encryption rnd = new Random();
	public static Encryption ope = new OrderPreserving();
	public static Encryption ah = new Homomorphic();
	public static Encryption det = new Deterministic();

	public static EncryptedData encrypt(int ptext, EncryptKind to) {
		Encryption toType = createEncryption(to);
		EncryptedData e = toType.encrypt(ptext);
		return e;
	}
	
	public static EncryptedData encryptSpe(int ptext, EncryptKind to) {
		String s = Integer.toString(ptext);
		return createEncryption(to).encrypt(s);
	}
	
	public static EncryptedData encrypt(String ptext, EncryptKind to) {
		Encryption toType = createEncryption(to);
		return toType.encrypt(ptext);
	}

	public static Object decrypt(EncryptedData ctext, EncryptKind from) {
		Encryption fromType = createEncryption(from);
		return fromType.decrypt(ctext);
	}
	
	public static EncryptedData convert(EncryptedData ctext, EncryptKind from, EncryptKind to) {
		Object ptext = decrypt(ctext, from);
		if (ptext instanceof String) {
			return encrypt((String) ptext, to);
		} else {
			return encrypt((int) ptext, to);
		}
	}
	
	public static EncryptedData convertSpe(EncryptedData ctext, EncryptKind from, EncryptKind to) {
		int ptext = (int) decrypt(ctext, from);
		String s = String.valueOf(ptext);
		return encrypt(s, to);
	}

	private static Encryption createEncryption(EncryptKind type) {
		switch (type) {
		case RND:
			return rnd;
		case OPE:
			return ope;
		case AH:
			return ah;
		default:
			assert (type == EncryptKind.DET);
			return det;
		}
	}

}
