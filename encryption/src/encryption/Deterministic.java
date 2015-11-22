package encryption;

import java.security.SecureRandom;
import java.math.BigInteger;

import encryption.EncryptedData.DataKind;

public class Deterministic implements Encryption {

	private final static BigInteger one = new BigInteger("1"), p, q;
	private final static SecureRandom random = new SecureRandom();
	private final static int N = 100;

	private BigInteger privateKey;
	private BigInteger publicKey;
	private BigInteger modulus;

	// generate an N-bit (roughly) public and private key
	static {
		p = BigInteger.probablePrime(N / 2, random);
		q = BigInteger.probablePrime(N / 2, random);
	}
	
	public Deterministic() {
		BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
		modulus = p.multiply(q);
		// common value in practice = 2^16 + 1
		publicKey = new BigInteger("65537"); 
		privateKey = publicKey.modInverse(phi);
	}

	@Override
	public EncryptedData encrypt(int ptext) {
		BigInteger message = BigInteger.valueOf(ptext);
		BigInteger encrypted = message.signum() == 1 ? message.modPow(publicKey, modulus)
				: message.negate().modPow(publicKey, modulus).negate();
		return new EncryptedData(DataKind.INT, encrypted);
	}

	@Override
	public Object decrypt(EncryptedData ctext) {
		BigInteger encrypted = ctext.getValue();
		if (ctext.getDataKind() == DataKind.INT) {
			return encrypted.signum() == 1 ?
					encrypted.modPow(privateKey, modulus).intValue()
					: -encrypted.negate().modPow(privateKey, modulus).intValue();
		} else {
			return new String(encrypted.modPow(privateKey, modulus).toByteArray());
		}
	}

	@Override
	public EncryptedData encrypt(String ptext) {
		BigInteger message = new BigInteger(ptext.getBytes());
		return new EncryptedData(DataKind.STRING, message.modPow(publicKey, modulus));
	}

}
