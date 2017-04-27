package encryption;

import java.nio.ByteBuffer;
import java.security.Key;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

public class DETEncryptor {

	private Key keyAES, keyBF; // AES and blowfish
	private Cipher cipherAES, cipherBF;
	private byte[] iv = new byte[8];
	private Encoder encoder = Base64.getEncoder();

	public DETEncryptor() {
		try {
			cipherAES = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipherBF = Cipher.getInstance("Blowfish/CBC/PKCS5PADDING");
			KeyGenerator generatorAES = KeyGenerator.getInstance("AES");
			KeyGenerator generatorBF = KeyGenerator.getInstance("Blowfish");
			// the keys need to be written to a file;
			// one RNDEncryptor object uses the same one key
			keyAES = generatorAES.generateKey();
			keyBF = generatorBF.generateKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String encrypt(int ptext) {
		byte[] cipher = encrypt(ByteBuffer.allocate(4).putInt(ptext).array(), cipherBF, keyBF);
		return encoder.encodeToString(cipher);
	}

	public String encrypt(long ptext) {
		byte[] cipher = encrypt(ByteBuffer.allocate(8).putLong(ptext).array(), cipherBF, keyBF);
		return encoder.encodeToString(cipher);
	}
	
	public String encrypt(String ptext) {
		byte[] cipher = encrypt(ptext.getBytes(), cipherAES, keyAES);
		return encoder.encodeToString(cipher);
	}

	private byte[] encrypt(byte[] ptext, Cipher cipher, Key key) {
		byte[] ctext = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			ctext = cipher.doFinal(ptext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ctext;
	}

}