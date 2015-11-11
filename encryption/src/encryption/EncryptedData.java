package encryption;

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
	private byte[] value;
	
	public EncryptedData(DataKind dataKind, EncryptKind encryptKind, byte[] value) {
		this.dataKind = dataKind;
		this.encryptKind = encryptKind;
		this.value = value;
	}
	
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
	public byte[] getValue() {
		return value;
	}
	public void setValue(byte[] value) {
		this.value = value;
	}
	
}
