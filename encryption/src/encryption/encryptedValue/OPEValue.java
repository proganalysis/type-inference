package encryption.encryptedValue;

public class OPEValue implements EncryptedValue {

	private String enInt;
	private String[] enString;
	
	public OPEValue(String[] enString) {
		this.enString = enString;
	}

	public OPEValue(String enInt) {
		this.enInt = enInt;
	}

	public String getEnInt() {
		return enInt;
	}

	public void setEnInt(String enInt) {
		this.enInt = enInt;
	}

	public String[] getEnString() {
		return enString;
	}

	public void setEnString(String[] enString) {
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
		OPEValue ct = (OPEValue) ei;
		if (ct.getEnInt() != null) {
			return enInt.compareTo(ct.getEnInt()) > 0;
		} else {
			for (int i = enString.length - 1; i > -1; i--) {
				if (enString[i].compareTo(ct.getEnString()[i]) > 0)
					return true;
			}
			return false;
		}
	}

	@Override
	public EncryptedValue add(int x) {
		// TODO Auto-generated method stub
		return null;
	}

}
