package encryption.encryptedValue;

public class RNDValue implements EncryptedValue {
	
	private byte[] enInt;
	private byte[][] enString;

	public RNDValue(byte[][] enString) {
		this.enString = enString;
	}

	public RNDValue(byte[] enInt) {
		this.enInt = enInt;
	}

	public byte[] getEnInt() {
		return enInt;
	}

	public void setEnInt(byte[] enInt) {
		this.enInt = enInt;
	}

	public byte[][] getEnString() {
		return enString;
	}

	public void setEnString(byte[][] enString) {
		this.enString = enString;
	}

	@Override
	public EncryptedValue add(EncryptedValue ei) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(EncryptedValue ei) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean largerThan(EncryptedValue ei) {
		// TODO Auto-generated method stub
		return false;
	}

}
