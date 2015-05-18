package encryption.encryptedValue;

public interface EncryptedValue {
	
	public EncryptedValue add(EncryptedValue ei);
	
	public EncryptedValue add(int x);
	
	public boolean equals(EncryptedValue ei);
	
	public boolean largerThan(EncryptedValue ei);
	
}
