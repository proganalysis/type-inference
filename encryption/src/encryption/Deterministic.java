package encryption;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;

import encryption.EncryptedData.DataKind;
import encryption.EncryptedData.EncryptKind;

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
	public EncryptedData encrypt(int ptext) {
		byte[] input = ByteBuffer.allocate(4).putInt(ptext).array();
		byte[] ctext = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			ctext = cipher.doFinal(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new EncryptedData(DataKind.INT, EncryptKind.DET, ctext);
	}
	
	@Override
	public EncryptedData encrypt(String ptext) {
		byte[] ctext = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			ctext = cipher.doFinal(ptext.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new EncryptedData(DataKind.STRING, EncryptKind.DET, ctext);
	}

	@Override
	public Object decrypt(EncryptedData ctext) {
		byte[] plainText = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			plainText = cipher.doFinal(ctext.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ctext.getDataKind() == DataKind.INT) {
			ByteBuffer wrapped = ByteBuffer.wrap(plainText);
			return wrapped.getInt();
		} else {
			return new String(plainText);
		}
	}

}
