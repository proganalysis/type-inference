package encryption.encryptedValue;

public interface EncryptedValue {
	
	//public EncryptedValue subtract(EncryptedValue ei);

	public EncryptedValue add(EncryptedValue ei);
	
	public boolean equals(EncryptedValue ei);
	
	public boolean largerThan(EncryptedValue ei);
	
}
