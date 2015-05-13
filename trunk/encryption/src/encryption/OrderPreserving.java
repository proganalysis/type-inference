package encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import encryption.encryptedValue.EncryptedValue;
import encryption.encryptedValue.OPEValue;

public class OrderPreserving implements Encryption {

	Process p;
	String line;
	BufferedReader in;

	private int decryptInt(String ctext) {
		try {
			p = Runtime.getRuntime().exec("lib/ope_decrypt " + ctext);
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(line);
	}

	@Override
	public Object decrypt(EncryptedValue ctext) {
		OPEValue ct = (OPEValue) ctext;
		String ctInt = ct.getEnInt();
		if (ctInt != null) { // int
			return decryptInt(ctInt);
		} else { // String
			String[] ctString = ct.getEnString();
			char[] ptext = new char[ctString.length];
			int i = 0;
			for (String s : ctString) {
				ptext[i] = (char) decryptInt(s);
				i++;
			}
			return new String(ptext);
		}
	}

	@Override
	public EncryptedValue encrypt(Object ptext) {
		if (ptext instanceof Integer) {
			int ptInt = (int) ptext;
			try {
				p = Runtime.getRuntime().exec("lib/ope_encrypt " + ptInt);
				in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new OPEValue(line);
		} else {
			assert(ptext instanceof String);
			String ptString = ptext.toString();
			String[] ctext = new String[ptString.length()];
			int i = 0;
			for (char c : ptString.toCharArray()) {
				ctext[i] = ((OPEValue) encrypt((int) c)).getEnInt();
				i++;
			}
			return new OPEValue(ctext);
		}
	}

}
