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
			long start = System.currentTimeMillis();
			Process p = Runtime.getRuntime().exec(
					"./ope_decrypt " + new String(ctext), envp, dir);
			long end = System.currentTimeMillis();
			System.out.println("exe " + (end - start));
			start = System.currentTimeMillis();
			p.waitFor();
			end = System.currentTimeMillis();
			System.out.println("wait " + (end - start));
			start = System.currentTimeMillis();
			InputStream input = p.getInputStream();
			byte[] targetArray = new byte[input.available()];
			input.read(targetArray);
			s = new String(Arrays.copyOf(targetArray, targetArray.length - 1));
			end = System.currentTimeMillis();
			System.out.println("other " + (end - start));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(s);
	}

//	public Object decrypt(EncryptedValue ctext) {
//		OPEValue ct = (OPEValue) ctext;
//		byte[] ctInt = ct.getEnInt();
//		if (ctInt != null) { // int
//			return decryptInt(ctInt);
//		} else { // String
//			byte[][] ctString = ct.getEnString();
//			char[] ptext = new char[ctString.length];
//			int i = 0;
//			for (byte[] s : ctString) {
//				ptext[i] = (char) decryptInt(s);
//				i++;
//			}
//			return new String(ptext);
//		}
//	}

	@Override
	public byte[] encrypt(int ptext) {
		byte[] targetArray = new byte[43];
		try {
			long start = System.currentTimeMillis();
			Process p = Runtime.getRuntime().exec("./ope_encrypt " + ptext,
					envp, dir);
			p.waitFor();
			long end = System.currentTimeMillis();
			System.out.println("ENCRYPT exe " + (end - start));
			start = System.currentTimeMillis();
			p.getInputStream().read(targetArray);
			end = System.currentTimeMillis();
			System.out.println("ENCRYPT read " + (end - start));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return targetArray;
	}

}
