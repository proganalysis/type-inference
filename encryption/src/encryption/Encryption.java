package encryption;

public interface Encryption {
	
	public Object encrypt(int ptext);
	public Object encrypt(String ptext);
	public Object decrypt(Object ctext);
	
}
