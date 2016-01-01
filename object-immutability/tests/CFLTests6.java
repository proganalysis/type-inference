interface I {
	public void set(String p);
	public String get();
}

class IData implements I {
	String d;
	public void set(String p) {
		this.d = p;
	}
	
	public String get() {
		return this.d;
	}	
}


public class CFLTests6 {
	public static void main() {
		I data = new IData();
		String s1 = new String("Ana");
		data.set(s1);
		String s2 = data.get();
		
		I data2 = new IData();
		String s3 = new String("Antun");
		data2.set(s3);
		String s4 = data2.get();
	}
}
