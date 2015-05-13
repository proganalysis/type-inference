package encryption;

import encryption.encryptedValue.EncryptedValue;

public interface Encryption {
	
	public EncryptedValue encrypt(Object ptext);
	public Object decrypt(EncryptedValue ctext);

}
