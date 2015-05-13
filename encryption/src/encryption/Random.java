package encryption;

import java.nio.ByteBuffer;
import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import encryption.encryptedValue.EncryptedValue;
import encryption.encryptedValue.RNDValue;

public class Random implements Encryption {

	private static final byte[] ivBytes = "1234567812345678".getBytes();
	private static final Key encryptionKey, decryptionKey;
	private Cipher cipher;

	static {
		KeyGenerator generator = null;
		try {
			generator = KeyGenerator.getInstance("AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
		generator.init(128);
		encryptionKey = generator.generateKey();
		decryptionKey = new SecretKeySpec(encryptionKey.getEncoded(),
				encryptionKey.getAlgorithm());
	}

	public Random() {
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int decryptInt(byte[] ctext) {
		byte[] plainText = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, decryptionKey,
					new IvParameterSpec(ivBytes));
			plainText = cipher.doFinal(ctext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOf(plainText, 4));
		return wrapped.getInt();
	}

	@Override
	public Object decrypt(EncryptedValue ctext) {
		RNDValue ct = (RNDValue) ctext;
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

	@Override
	public EncryptedValue encrypt(Object ptext) {
		if (ptext instanceof Integer) {
			int ptInt = (int) ptext;
			byte[] input = ByteBuffer.allocate(4).putInt(ptInt).array();
			byte[] ctext = null;
			try {
				cipher.init(Cipher.ENCRYPT_MODE, encryptionKey,
						new IvParameterSpec(ivBytes));
				ctext = cipher.doFinal(input);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new RNDValue(ctext);
		} else {
			assert(ptext instanceof String);
			String ptString = ptext.toString();
			byte[][] ctext = new byte[ptString.length()][];
			int i = 0;
			for (char c : ptString.toCharArray()) {
				ctext[i] = ((RNDValue) encrypt((int) c)).getEnInt();
				i++;
			}
			return new RNDValue(ctext);
		}
	}

}
