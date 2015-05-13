package encryption;

import java.math.BigInteger;

import encryption.encryptedValue.AHValue;
import encryption.encryptedValue.EncryptedValue;
import thep.paillier.EncryptedInteger;
import thep.paillier.PrivateKey;
import thep.paillier.PublicKey;
import thep.paillier.exceptions.BigIntegerClassNotValid;

public class Homomorphic implements Encryption {

	private static final PrivateKey priv = new PrivateKey(1024);
	private static final PublicKey pub = priv.getPublicKey();
	
	public PublicKey getPubKey() {
		return pub;
	}
	
//	@Override
//	public EncryptedValue encrypt(Object ptext) {
//		int ptInt = (int) ptext;
//		boolean isNeg = false;
//		if (ptInt < 0) {
//			ptInt = -ptInt;
//			isNeg = true;
//		}
//		BigInteger tmp = BigInteger.valueOf(ptInt);
//		tmp = tmp.mod(pub.getN());
//		EncryptedInteger eInt = null;
//		try {
//			eInt = new EncryptedInteger(tmp, pub);
//		} catch (BigIntegerClassNotValid e) {
//			e.printStackTrace();
//		}
//		return new AHValue(eInt, isNeg);
//	}

	@Override
	public EncryptedValue encrypt(Object ptext) {
		int ptInt = (int) ptext;
		BigInteger tmp = BigInteger.valueOf(ptInt);
		tmp = tmp.mod(pub.getN());
		EncryptedInteger eInt = null;
		try {
			eInt = new EncryptedInteger(tmp, pub);
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
		return new AHValue(eInt);
	}
	
	@Override
	public Object decrypt(EncryptedValue ctext) {
		AHValue ct = (AHValue) ctext;
		EncryptedInteger ctInt = ct.getEnInt();
		BigInteger ptext = null;
		try {
			ptext = ctInt.decrypt(priv);
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
		return ptext.intValue();
	}

}
