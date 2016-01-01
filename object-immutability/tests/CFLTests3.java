class Data {
	String d;
	public void set(String p) {
		this.d = p;
	}
	
	public String get() {
		return this.d;
	}	
}


public class CFLTests3 {
	public static void main() {
		Data data = new Data();
		String s1 = new String("Ana");
		data.set(s1);
		String s2 = data.get();
		
		Data data2 = new Data();
		String s3 = new String("Antun");
		data2.set(s3);
		String s4 = data2.get();
	}
}
