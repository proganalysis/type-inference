package encryption;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OrderPreserving implements Encryption {

	@Override
	public Object decrypt(Object ctext) {
		ArrayList<String> array = new ArrayList<>();
		try {
			ProcessBuilder pb = new ProcessBuilder("python", "lib/ope_decrypt.py", ctext.toString());
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
			char[] charArray = new char[array.size()-1];
			for (int i = 0; i < array.size() - 1; i++) {
				charArray[i] = (char) Integer.parseInt(array.get(i));
			}
			return new String(charArray);
		}
	}

	@Override
	public String encrypt(String ptext) {
		String res = "";
		try {
			ProcessBuilder pb = new ProcessBuilder("python", "lib/ope_encrypt_String.py", ptext);
			Process p = pb.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s;
			while ((s = in.readLine()) != null) {
				res += s + " ";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public String encrypt(int ptext) {
		String s = null;
		try {
			ProcessBuilder pb = new ProcessBuilder("python", "lib/ope_encrypt.py", "" + ptext);
			Process p = pb.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			s = in.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

}
