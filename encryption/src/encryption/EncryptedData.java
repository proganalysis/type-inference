package encryption;

import java.math.BigInteger;

public class EncryptedData {
	
	public enum DataKind {
		INT,
		STRING
	}
	
	public enum EncryptKind {
		RND,
		AH,
		OPE,
		DET
	}
	
	private DataKind dataKind;
	private EncryptKind encryptKind;
	private BigInteger value;
	
	public EncryptedData(DataKind dataKind, EncryptKind encryptKind, BigInteger value) {
		this.dataKind = dataKind;
		this.encryptKind = encryptKind;
		this.value = value;
	}
	
//	public EncryptedData(DataKind dataKind, BigInteger value) {
//		this.dataKind = dataKind;
//		this.value = value;
//	}
	
	public DataKind getDataKind() {
		return dataKind;
	}
	public void setDataKind(DataKind dataKind) {
		this.dataKind = dataKind;
	}
	public EncryptKind getEncryptKind() {
		return encryptKind;
	}
	public void setEncryptKind(EncryptKind encryptKind) {
		this.encryptKind = encryptKind;
	}
	public BigInteger getValue() {
		return value;
	}
	public void setValue(BigInteger value) {
		this.value = value;
	}
	
}
