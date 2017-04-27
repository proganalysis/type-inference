package encryption;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

import java.nio.ByteBuffer;

public class Deterministic implements Encryption {

	private static final Key keyAES, keyBF; // AES and blowfish
	private Cipher cipherAES, cipherBF;
	private static Map<byte[], byte[]> initIVsAES = new HashMap<>();
	private static Map<byte[], byte[]> initIVsBF = new HashMap<>();

	static {
		KeyGenerator generatorAES = null, generatorBF = null;
		try {
			generatorAES = KeyGenerator.getInstance("AES");
			generatorBF = KeyGenerator.getInstance("Blowfish");
		} catch (Exception e) {
			e.printStackTrace();
		}
		keyAES = generatorAES.generateKey();
		keyBF = generatorBF.generateKey();
	}

	public Deterministic() {
		try {
			cipherAES = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipherBF = Cipher.getInstance("Blowfish/CBC/PKCS5PADDING");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] encrypt(String ptext) {
		byte[] ctext = null;
		try {
			cipherAES.init(Cipher.ENCRYPT_MODE, keyAES);
			ctext = cipherAES.doFinal(ptext.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		initIVsAES.put(ctext, cipherAES.getIV());
		return ctext;
	}

	public byte[] encrypt(long ptext) {
		byte[] ctext = null;
		try {
			cipherBF.init(Cipher.ENCRYPT_MODE, keyBF);
			ctext = cipherBF.doFinal(ByteBuffer.allocate(8).putLong(ptext).array());
		} catch (Exception e) {
			e.printStackTrace();
		}
		initIVsBF.put(ctext, cipherBF.getIV());
		return ctext;
	}

	@Override
	public Object decrypt(Object ctext) {
		byte[] plainText = null;
		byte[] ciphertext = (byte[]) ctext;
		if (ciphertext.length == 8) { // blowfish: 64-bit block size
			try {
				cipherBF.init(Cipher.DECRYPT_MODE, keyBF, new IvParameterSpec(initIVsBF.get(ctext)));
				plainText = cipherBF.doFinal(ciphertext);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ByteBuffer wrapped = ByteBuffer.wrap(plainText);
			return wrapped.getLong();
		} else { // AES: 128-bit block size
			try {
				cipherAES.init(Cipher.DECRYPT_MODE, keyAES, new IvParameterSpec(initIVsAES.get(ctext)));
				plainText = cipherAES.doFinal(ciphertext);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new String(plainText);
		}
	}

	@Override
	public Object encrypt(int ptext) {
		return encrypt((long) ptext);
	}
}
