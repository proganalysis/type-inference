package encryption;

public abstract class Encryption {
	
	public abstract byte[] encrypt(int pint);
	public abstract int decrypt(byte[] cint);
	
	public byte[][] encrypt(String pstring) {
		byte[][] ctext = new byte[pstring.length()][];
		int i = 0;
		for (char c : pstring.toCharArray()) {
			ctext[i] = encrypt(c);
			i++;
		}
		return ctext;
	}
	public String decrypt(byte[][] cstring) {
		char[] ptext = new char[cstring.length];
		int i = 0;
		for (byte[] b : cstring) {
			ptext[i] = (char) decrypt(b);
			i++;
		}
		return new String(ptext);
	}
}
