package encryption;

import java.math.BigInteger;

import thep.paillier.EncryptedInteger;
import thep.paillier.PrivateKey;
import thep.paillier.PublicKey;
import thep.paillier.exceptions.BigIntegerClassNotValid;

public class Homomorphic implements Encryption {

	private static final PrivateKey priv = new PrivateKey(1024);
	private static final PublicKey pub = priv.getPublicKey();
	public static EncryptedInteger ei;
	static {
		try {
			ei = new EncryptedInteger(pub);
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public BigInteger encrypt(int ptext) {
		try {
			ei.set(BigInteger.valueOf(Math.abs(ptext)));
			if (ptext < 0) {
				ei.set(BigInteger.valueOf(-ptext));
				ei = ei.multiply(new BigInteger("-1"));
			} else {
				ei.set(BigInteger.valueOf(ptext));
			}
		} catch (BigIntegerClassNotValid e1) {
			e1.printStackTrace();
		}
		return ei.getCipherVal();
	}
	
	@Override
	public Object decrypt(Object ctext) {
		ei.setCipherVal((BigInteger) ctext);
		BigInteger ptext = null;
		try {
			ptext = ei.decrypt(priv);
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
		if (ptext.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
			ptext = ptext.subtract(pub.getN());
		}
		return ptext.intValue();
	}

	@Override
	public Object encrypt(String ptext) {
		return null;
	}

}
