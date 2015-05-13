package encryption.encryptedValue;

import thep.paillier.EncryptedInteger;

public class AHValue implements EncryptedValue {

	private EncryptedInteger enInt;
	//private boolean isNeg;

//	public AHValue(EncryptedInteger enInt, boolean isNeg) {
//		this.enInt = enInt;
//		this.setNeg(isNeg);
//	}
	
	public AHValue(EncryptedInteger enInt) {
		this.enInt = enInt;
	}

	public void setEnInt(EncryptedInteger enInt) {
		this.enInt = enInt;
	}

	public EncryptedInteger getEnInt() {
		return enInt;
	}

//	public boolean isNeg() {
//		return isNeg;
//	}
//
//	public void setNeg(boolean isNeg) {
//		this.isNeg = isNeg;
//	}

//	private AHValue addPositive(AHValue val) {
//		EncryptedInteger ct = val.getEnInt();
//		try {
//			enInt = enInt.add(ct);
//		} catch (PublicKeysNotEqualException e) {
//			e.printStackTrace();
//		}
//		return this;
//	}
//
//	private AHValue subtractPositive(AHValue val) {
//		EncryptedInteger ct = val.getEnInt();
//		try {
//			ct = ct.multiply(new BigInteger("-1"));
//			enInt = enInt.add(ct);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		return this;
//	}
//
//	@Override
//	public EncryptedValue subtract(EncryptedValue ei) {
//		AHValue result;
//		AHValue ct = (AHValue) ei;
//		boolean isLarger = enInt.getCipherVal().compareTo(
//				ct.getEnInt().getCipherVal()) > 0;
//		if (!isNeg && !ct.isNeg()) {
//			if (isLarger) {
//				result = subtractPositive(ct);
//				result.setNeg(false);
//			} else {
//				result = ct.subtractPositive(this);
//				result.setNeg(true);
//			}
//		} else if (isNeg && ct.isNeg()) {
//			if (isLarger) {
//				result = ct.subtractPositive(this);
//				result.setNeg(true);
//			} else {
//				result = subtractPositive(ct);
//				result.setNeg(false);
//			}
//		} else if (!isNeg && ct.isNeg()) {
//			result = addPositive(ct);
//			result.setNeg(false);
//		} else {
//			result = addPositive(ct);
//			result.setNeg(true);
//		}
//		return result;
//	}
//
//	@Override
//	public EncryptedValue add(EncryptedValue ei) {
//		AHValue result;
//		AHValue ct = (AHValue) ei;
//		boolean isLarger = enInt.getCipherVal().compareTo(
//				ct.getEnInt().getCipherVal()) > 0;
//		if (!isNeg && ct.isNeg()) {
//			if (isLarger) {
//				result = subtractPositive(ct);
//				result.setNeg(false);
//			} else {
//				result = ct.subtractPositive(this);
//				result.setNeg(true);
//			}
//		} else if (isNeg && !ct.isNeg()) {
//			if (isLarger) {
//				result = ct.subtractPositive(this);
//				result.setNeg(true);
//			} else {
//				result = subtractPositive(ct);
//				result.setNeg(false);
//			}
//		} else if (!isNeg && !ct.isNeg()) {
//			result = addPositive(ct);
//			result.setNeg(false);
//		} else {
//			result = addPositive(ct);
//			result.setNeg(true);
//		}
//		return result;
//	}

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

}
