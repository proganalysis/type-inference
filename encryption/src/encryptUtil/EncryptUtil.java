package encryptUtil;

import java.math.BigInteger;

import jope.OPE;
import thep.paillier.EncryptedInteger;
import thep.paillier.PrivateKey;
import thep.paillier.PublicKey;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;

public class EncryptUtil {
	
	private static OPE ope = new OPE();
	private static PrivateKey priv = new PrivateKey(64);
	private static PublicKey pub = priv.getPublicKey();
	
	public static String getOPE(int clear) {
		BigInteger clearBig = new BigInteger("" + clear);
		return ope.encrypt(clearBig).toString();
	}
	
	public static String getOPE(double clear) {
		BigInteger clearBig = new BigInteger("" + (long) clear);
		return ope.encrypt(clearBig).toString();
	}
	
	public static String getAH(double clear) throws BigIntegerClassNotValid {
		EncryptedInteger eclear = new EncryptedInteger(BigInteger.valueOf((long) clear), pub);
		return eclear.getCipherVal().toString();
	}
	
	public static String getAH(int clear) throws BigIntegerClassNotValid {
		EncryptedInteger eclear = new EncryptedInteger(BigInteger.valueOf(clear), pub);
		return eclear.getCipherVal().toString();
	}
	
	public static boolean isGt(String a, int b) {
		return a.compareTo(getOPE(b)) > 0;
	}
	
	public static boolean isGt(String a, String b) {
		return a.compareTo(b) > 0;
	}
	
	public static boolean isGt(int a, String b) {
		return getOPE(a).compareTo(b) > 0;
	}
	
	public static boolean isLt(String a, int b) {
		return a.compareTo(getOPE(b)) < 0;
	}
	
	public static String add(String a, int b) throws BigIntegerClassNotValid {
		EncryptedInteger ea = new EncryptedInteger(pub);
		ea.setCipherVal(new BigInteger(a));
		EncryptedInteger esum = ea.add(BigInteger.valueOf(b));
		return esum.getCipherVal().toString();
	}
	
	public static String add(int a, String b) throws BigIntegerClassNotValid {
		EncryptedInteger eb = new EncryptedInteger(pub);
		eb.setCipherVal(new BigInteger(b));
		EncryptedInteger esum = eb.add(BigInteger.valueOf(a));
		return esum.getCipherVal().toString();
	}
	
	public static String add(String a, String b) throws BigIntegerClassNotValid {
		EncryptedInteger ea = new EncryptedInteger(pub);
		ea.setCipherVal(new BigInteger(a));
		EncryptedInteger eb = new EncryptedInteger(pub);
		eb.setCipherVal(new BigInteger(b));
		try {
			ea = ea.add(eb);
		} catch (PublicKeysNotEqualException e) {
			
		}
		return ea.getCipherVal().toString();
	}
}
