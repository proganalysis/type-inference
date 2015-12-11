package encryption;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import java.math.BigInteger;

public class Deterministic implements Encryption {

	// for int
	private final static BigInteger one = new BigInteger("1"), p, q;
	private final static SecureRandom random = new SecureRandom();
	private final static int N = 100;
	private BigInteger privateKey, publicKey, modulus;

	// for String
	private static final Key publicKey2, privateKey2;
	private Cipher cipher; 

	// generate an N-bit (roughly) public and private key
	static {
		// for int
		p = BigInteger.probablePrime(N / 2, random);
		q = BigInteger.probablePrime(N / 2, random);
		
		// for String
		KeyPairGenerator kpg = null;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		kpg.initialize(1024);
		KeyPair kp = kpg.generateKeyPair();
		publicKey2 = kp.getPublic();
		privateKey2 = kp.getPrivate();
	}

	public Deterministic() {
		// for int
		BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
		modulus = p.multiply(q);
		// common value in practice = 2^16 + 1
		publicKey = new BigInteger("65537");
		privateKey = publicKey.modInverse(phi);
		
		// for String
		try {
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public BigInteger encrypt(int ptext) {
		BigInteger message = BigInteger.valueOf(ptext);
		BigInteger encrypted = message.signum() == 1 ? message.modPow(publicKey, modulus)
				: message.negate().modPow(publicKey, modulus).negate();
		return encrypted;
	}

	@Override
	public Object decrypt(Object ctext) {
		if (ctext instanceof BigInteger) {
			BigInteger encrypted = (BigInteger) ctext;
			return encrypted.signum() == 1 ? encrypted.modPow(privateKey, modulus).intValue()
					: -encrypted.negate().modPow(privateKey, modulus).intValue();
		} else {
			byte[] ptext = null;
			try {
				cipher.init(Cipher.DECRYPT_MODE, privateKey2);
				ptext = cipher.doFinal((byte[]) ctext);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new String(ptext).trim();
		}
	}

	@Override
	public byte[] encrypt(String ptext) {
		byte[] ctext = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, publicKey2);
			ctext = cipher.doFinal(ptext.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ctext;
	}

}
