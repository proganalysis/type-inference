package encryption;

public interface Encryption {
	
	public EncryptedData encrypt(int ptext);
	public EncryptedData encrypt(String ptext);
	public Object decrypt(EncryptedData ctext);
	
//	public byte[][] encrypt(String pstring) {
//		byte[][] ctext = new byte[pstring.length()][];
//		int i = 0;
//		for (char c : pstring.toCharArray()) {
//			ctext[i] = encrypt(c);
//			i++;
//		}
//		return ctext;
//	}
//	public String decrypt(byte[][] cstring) {
//		char[] ptext = new char[cstring.length];
//		int i = 0;
//		for (byte[] b : cstring) {
//			ptext[i] = (char) decrypt(b);
//			i++;
//		}
//		return new String(ptext);
//	}
}
