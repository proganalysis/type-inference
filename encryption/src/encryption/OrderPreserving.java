package encryption;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class OrderPreserving implements Encryption {
	
	private static String prg
	
	try{
		 
		String prg = "import sys\nprint&nbsp;int(sys.argv[1])+int(sys.argv[2])\n";
		BufferedWriter out = new BufferedWriter(new FileWriter("test1.py"));
		out.write(prg);
		out.close();
		int number1 = 10;
		int number2 = 32;
		 
		ProcessBuilder pb = new ProcessBuilder("python","test1.py",""+number1,""+number2);
		Process p = pb.start();
		 
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		int ret = new Integer(in.readLine()).intValue();
		System.out.println("value is : "+ret);
		}catch(Exception e){System.out.println(e);}
		}

//	private static File dir = new File("lib");
//	private static String[] envp = new String[] { "LD_LIBRARY_PATH=." };
//
//	@Override
//	public int decrypt(byte[] ctext) {
//		String s = null;
//		try {
//			Process p = Runtime.getRuntime().exec(
//					"./ope_decrypt " + new String(ctext), envp, dir);
//			p.waitFor();
//			InputStream input = p.getInputStream();
//			byte[] targetArray = new byte[input.available()];
//			input.read(targetArray);
//			s = new String(Arrays.copyOf(targetArray, targetArray.length - 1));
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return Integer.parseInt(s);
//	}
//
//	@Override
//	public byte[] encrypt(int ptext) {
//		byte[] targetArray = new byte[100];
//		int num = 0;
//		try {
//			Process p = Runtime.getRuntime().exec("./ope_encrypt " + ptext,
//					envp, dir);
//			p.waitFor();
//			num = p.getInputStream().read(targetArray);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return Arrays.copyOf(targetArray, num);
//	}

}
