package encryption;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class OrderPreserving extends Encryption {

	private static File dir = new File("lib");
	private static String[] envp = new String[] { "LD_LIBRARY_PATH=." };

	@Override
	public int decrypt(byte[] ctext) {
		String s = null;
		try {
			Process p = Runtime.getRuntime().exec(
					"./ope_decrypt " + new String(ctext), envp, dir);
			p.waitFor();
			InputStream input = p.getInputStream();
			byte[] targetArray = new byte[input.available()];
			input.read(targetArray);
			s = new String(Arrays.copyOf(targetArray, targetArray.length - 1));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(s);
	}

	@Override
	public byte[] encrypt(int ptext) {
		byte[] targetArray = new byte[100];
		int num = 0;
		try {
			Process p = Runtime.getRuntime().exec("./ope_encrypt " + ptext,
					envp, dir);
			p.waitFor();
			num = p.getInputStream().read(targetArray);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Arrays.copyOf(targetArray, num);
	}

}
