package encryption;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import com.n1analytics.paillier.PaillierContext;
import com.n1analytics.paillier.PaillierPrivateKey;
import com.n1analytics.paillier.PaillierPublicKey;
import com.n1analytics.paillier.cli.PrivateKeyJsonSerialiser;

public class AHEncryptor {

	private PaillierPrivateKey privateKey;
	private PaillierPublicKey publicKey;
	private PaillierContext context;

	public AHEncryptor(String outputDir) {
		privateKey = PaillierPrivateKey.create(64);
		publicKey = privateKey.getPublicKey();
		context = publicKey.createSignedContext();
		PrivateKeyJsonSerialiser serializedPrivateKey = new PrivateKeyJsonSerialiser(null);
		privateKey.serialize(serializedPrivateKey);
		String privKeyFile = outputDir + "/key.priv";
		try (PrintWriter out = new PrintWriter(privKeyFile)) {
			out.println(serializedPrivateKey);
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find that location sorry.");
		}
		try (PrintWriter out = new PrintWriter(outputDir + "/key.pub")) {
			out.println(publicKey.getModulus());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String encrypt(String ptext) {
		return Util.getAHString(context.encrypt(Integer.parseInt(ptext)));
	}

	public String encrypt(int ptext) {
		return Util.getAHString(context.encrypt(ptext));
	}
	
	public String encrypt(double ptext) {
		return Util.getAHString(context.encrypt(ptext));
	}
	
}
