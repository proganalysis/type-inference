package encryption.encryptedValue;

import java.util.Arrays;

public class DETValue implements EncryptedValue {

	private byte[] enInt;
	private byte[][] enString;

	public DETValue(byte[] enInt) {
		this.enInt = enInt;
	}

	public DETValue(byte[][] enString) {
		this.enString = enString;
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
		DETValue ct = (DETValue) ei;
		if (enInt != null) {
			return Arrays.equals(enInt, ct.getEnInt());
		} else {
			return Arrays.deepEquals(enString, ct.enString);
		}
	}

	@Override
	public boolean largerThan(EncryptedValue ei) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EncryptedValue add(int x) {
		// TODO Auto-generated method stub
		return null;
	}

}
