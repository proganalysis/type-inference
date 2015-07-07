package encryption;

import java.nio.ByteBuffer;
import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Random extends Encryption {

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

	@Override
	public int decrypt(byte[] ctext) {
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
	public byte[] encrypt(int ptext) {
		byte[] input = ByteBuffer.allocate(4).putInt(ptext).array();
		byte[] ctext = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, encryptionKey,
					new IvParameterSpec(ivBytes));
			ctext = cipher.doFinal(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ctext;
	}

}
