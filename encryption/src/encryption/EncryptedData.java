package encryption;

import java.io.Serializable;
import java.math.BigInteger;

public class EncryptedData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public enum DataKind {
		INT,
		STRING
	}
	
	private DataKind dataKind;
	private String encryptKind;
	private BigInteger value;
	
	public EncryptedData(DataKind dataKind, String encryptKind, BigInteger value) {
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
	public String getEncryptKind() {
		return encryptKind;
	}
	public void setEncryptKind(String encryptKind) {
		this.encryptKind = encryptKind;
	}
	public BigInteger getValue() {
		return value;
	}
	public void setValue(BigInteger value) {
		this.value = value;
	}

}
