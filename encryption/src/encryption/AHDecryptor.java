package encryption;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1analytics.paillier.EncodedNumber;
import com.n1analytics.paillier.EncryptedNumber;
import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.cli.SerialisationUtil;

public class AHDecryptor {

	private PaillierPrivateKey privateKey;
	private PaillierContext context;

	public AHDecryptor(String privKeyFile) {
		final ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("rawtypes")
		Map privateKeyData;
		try {
			privateKeyData = mapper.readValue(new File(privKeyFile), Map.class);
			privateKey = SerialisationUtil.unserialise_private(privateKeyData);
			context = privateKey.getPublicKey().createSignedContext();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public EncodedNumber decrypt(String cipherStr) {
		EncryptedNumber cipher = Util.getAHCipher(cipherStr, context);
		return cipher.decrypt(privateKey);
	}
	
}
