class DD {
	String f;
}


public class CFLTests7 {

	public static DD dd = new DD();
	
	public static String theString;
	
	public static void main(String[] arg) {
		String s1 = new String("Ana");
		String s2 = new String("Antun");
		
		theString = s1;
		dd.f = s2;
		
		m();
		n();		
	}
	
	public static void m() {
		String s3 = theString;
	}
	
	public static void n() {
		String s4 = dd.f;
	}
}
