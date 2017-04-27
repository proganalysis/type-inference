package encryption;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

public class Random {

	private static Key keyAES, keyBF; // AES and blowfish
	private static Cipher cipherAES, cipherBF;

	static {
		try {
			cipherAES = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipherBF = Cipher.getInstance("Blowfish/CBC/PKCS5PADDING");
			KeyGenerator generatorAES = KeyGenerator.getInstance("AES");
			KeyGenerator generatorBF = KeyGenerator.getInstance("Blowfish");
			keyAES = generatorAES.generateKey();
			keyBF = generatorBF.generateKey();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] encrypt(byte[] ptext) {
		byte[] ctext = null;
		try {
			if (ptext.length == 4) {
				cipherBF.init(Cipher.ENCRYPT_MODE, keyBF);
				ctext = cipherBF.doFinal(ptext);
			} else {
				cipherAES.init(Cipher.ENCRYPT_MODE, keyAES);
				ctext = cipherAES.doFinal(ptext);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ctext;
	}

	public byte[] decrypt(byte[] ctext) {
		byte[] plainText = null;
		try {
			if (ctext.length == 8) { // blowfish: 64-bit block size
				cipherBF.init(Cipher.DECRYPT_MODE, keyBF, new IvParameterSpec(cipherBF.getIV()));
				plainText = cipherBF.doFinal(ctext);
			} else { // AES: 128-bit block size
				cipherAES.init(Cipher.DECRYPT_MODE, keyAES, new IvParameterSpec(cipherAES.getIV()));
				plainText = cipherAES.doFinal(ctext);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plainText;
	}

}