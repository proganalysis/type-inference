package encryption;

import java.math.BigInteger;

import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;

public class Util {

	public static EncryptedNumber getAHCipher(String cipherStr, PaillierContext context) {
		int index = cipherStr.indexOf('#');
		BigInteger cipher = new BigInteger(cipherStr.substring(0, index));
		int exp = Integer.parseInt(cipherStr.substring(index + 1));
		return new EncryptedNumber(context, cipher, exp);
	}
	
	public static String getAHString(EncryptedNumber cipher) {
		return cipher.calculateCiphertext() + "#" + cipher.getExponent();
	}
	
}
