package encryption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import encryption.EncryptedData.DataKind;
import encryption.EncryptedData.EncryptKind;

public class OrderPreserving implements Encryption {

	@Override
	public Object decrypt(EncryptedData ctext) {
		ArrayList<String> array = new ArrayList<>();
		try {
			ProcessBuilder pb = new ProcessBuilder("python", "lib/ope_decrypt.py", new String(ctext.getValue()));
			Process p = pb.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s;
			while ((s = in.readLine()) != null) {
				array.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (array.size() == 1) {
			return Integer.parseInt(array.get(0));
		} else {
			char[] charArray = new char[array.size()];
			for (int i = 0; i < array.size(); i++) {
				charArray[i] = (char) Integer.parseInt(array.get(i));
			}
			return new String(charArray);
		}
	}

	@Override
	public EncryptedData encrypt(String ptext) {
		String res = "";
		try {
			ProcessBuilder pb = new ProcessBuilder("python", "lib/ope_encrypt_String.py", "" + ptext);
			Process p = pb.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s;
			while ((s = in.readLine()) != null) {
				res += s + " ";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new EncryptedData(DataKind.INT, EncryptKind.OPE, res.getBytes());
	}

	@Override
	public EncryptedData encrypt(int ptext) {
		String s = null;
		try {
			ProcessBuilder pb = new ProcessBuilder("python", "lib/ope_encrypt.py", "" + ptext);
			Process p = pb.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			s = in.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new EncryptedData(DataKind.INT, EncryptKind.OPE, s.getBytes());
	}

}
