package encryption;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

import encryption.EncryptedData.DataKind;

public class Random implements Encryption {

	private static final Key keyAES, keyBF; // AES and blowfish
	private Cipher cipherAES, cipherBF;
	private static Map<EncryptedData, byte[]> initIVsAES = new HashMap<>();
	private static Map<EncryptedData, byte[]> initIVsBF = new HashMap<>();

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

	public Random() {
		try {
			cipherAES = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipherBF = Cipher.getInstance("Blowfish/CBC/PKCS5PADDING");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EncryptedData encrypt(String ptext) {
		byte[] ctext = null;
		try {
			cipherAES.init(Cipher.ENCRYPT_MODE, keyAES);
			ctext = cipherAES.doFinal(ptext.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EncryptedData encrypted = new EncryptedData(DataKind.STRING, "RND", new BigInteger(ctext));
		initIVsAES.put(encrypted, cipherAES.getIV());
		return encrypted;
	}

	public EncryptedData encrypt(int ptext) {
		byte[] ctext = null;
		try {
			cipherBF.init(Cipher.ENCRYPT_MODE, keyBF);
			ctext = cipherBF.doFinal(ByteBuffer.allocate(4).putInt(ptext).array());
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		EncryptedData encrypted = new EncryptedData(DataKind.INT, "RND", new BigInteger(ctext));
		initIVsBF.put(encrypted, cipherBF.getIV());
		return encrypted;
	}

	@Override
	public Object decrypt(EncryptedData ctext) {
		byte[] plainText = null;
		if (ctext.getDataKind() == DataKind.INT) { // blowfish: 64-bit block
													// size
			try {
				cipherBF.init(Cipher.DECRYPT_MODE, keyBF, new IvParameterSpec(initIVsBF.get(ctext)));
				plainText = cipherBF.doFinal(ctext.getValue().toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
			ByteBuffer wrapped = ByteBuffer.wrap(plainText);
			return wrapped.getInt();
		} else { // AES: 128-bit block size
			try {
				cipherAES.init(Cipher.DECRYPT_MODE, keyAES, new IvParameterSpec(initIVsAES.get(ctext)));
				plainText = cipherAES.doFinal(ctext.getValue().toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new String(plainText);
		}
	}

}