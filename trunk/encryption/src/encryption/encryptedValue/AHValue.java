package encryption.encryptedValue;

import java.math.BigInteger;

import thep.paillier.EncryptedInteger;
import thep.paillier.exceptions.BigIntegerClassNotValid;

public class AHValue implements EncryptedValue {

	private EncryptedInteger enInt;

	public AHValue(EncryptedInteger enInt) {
		this.enInt = enInt;
	}

	public void setEnInt(EncryptedInteger enInt) {
		this.enInt = enInt;
	}

	public EncryptedInteger getEnInt() {
		return enInt;
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

	@Override
	public EncryptedValue add(EncryptedValue ei) {
		AHValue ct = (AHValue) ei;
		try {
			enInt = enInt.add(ct.getEnInt());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public EncryptedValue add(int x) {
		try {
			enInt = enInt.add(BigInteger.valueOf(x));
		} catch (BigIntegerClassNotValid e) {
			e.printStackTrace();
		}
		return this;
	}

}
