package encryption;

public class Conversion {
	
	public static Encryption rnd = new Random();
	public static Encryption ope = new OrderPreserving();
	public static Encryption ah = new Homomorphic();
	public static Encryption det = new Deterministic();

	public static EncryptedData encrypt(int ptext, String to) {
		Encryption toType = createEncryption(to);
		EncryptedData e = toType.encrypt(ptext);
		return e;
	}
	
	public static EncryptedData encryptSpe(int ptext, String to) {
		String s = Integer.toString(ptext);
		return createEncryption(to).encrypt(s);
	}
	
	public static EncryptedData encrypt(String ptext, String to) {
		Encryption toType = createEncryption(to);
		return toType.encrypt(ptext);
	}

	public static Object decrypt(EncryptedData ctext, String from) {
		Encryption fromType = createEncryption(from);
		return fromType.decrypt(ctext);
	}
	
	public static EncryptedData convert(EncryptedData ctext, String from, String to) {
		Object ptext = decrypt(ctext, from);
		if (ptext instanceof String) {
			return encrypt((String) ptext, to);
		} else {
			return encrypt((int) ptext, to);
		}
	}
	
	public static EncryptedData convertSpe(EncryptedData ctext, String from, String to) {
		int ptext = (int) decrypt(ctext, from);
		String s = String.valueOf(ptext);
		return encrypt(s, to);
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
