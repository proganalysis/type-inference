package encryption;

public interface Encryption {
	
	public EncryptedData encrypt(int ptext);
	public EncryptedData encrypt(String ptext);
	public Object decrypt(EncryptedData ctext);
	
}
