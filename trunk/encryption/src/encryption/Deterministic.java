package encryption;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;

import encryption.encryptedValue.DETValue;
import encryption.encryptedValue.EncryptedValue;

public class Deterministic implements Encryption {

	private static final Key publicKey, privateKey;
	private Cipher cipher;

	static {
		KeyPairGenerator kpg = null;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		kpg.initialize(1024);
		KeyPair kp = kpg.genKeyPair();
		publicKey = kp.getPublic();
		privateKey = kp.getPrivate();
	}

	public Deterministic() {
		try {
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public EncryptedValue encrypt(Object ptext) {
		if (ptext instanceof Integer) {
			int ptInt = (int) ptext;
			byte[] input = ByteBuffer.allocate(4).putInt(ptInt).array();
			byte[] ctext = null;
			try {
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
				ctext = cipher.doFinal(input);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new DETValue(ctext);
		} else {
			assert(ptext instanceof String);
			String ptString = ptext.toString();
			byte[][] ctext = new byte[ptString.length()][];
			int i = 0;
			for (char c : ptString.toCharArray()) {
				ctext[i] = ((DETValue) encrypt((int) c)).getEnInt();
				i++;
			}
			return new DETValue(ctext);
		}
	}

	private int decryptInt(byte[] ctext) {
		byte[] plainText = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			plainText = cipher.doFinal(ctext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(plainText,
				plainText.length - 4, plainText.length));
		return wrapped.getInt();
	}

	@Override
	public Object decrypt(EncryptedValue ctext) {
		DETValue ct = (DETValue) ctext;
		byte[] ctInt = ct.getEnInt();
		if (ctInt != null) { // int
			return decryptInt(ctInt);
		} else { // String
			byte[][] ctString = ct.getEnString();
			char[] ptext = new char[ctString.length];
			int i = 0;
			for (byte[] b : ctString) {
				ptext[i] = (char) decryptInt(b);
				i++;
			}
			return new String(ptext);
		}
	}

}
