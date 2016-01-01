class MyB {
	String f;
}

public class CFLTests2 {
	
	public static MyB id(MyB p) {
		return p;
	}
	
	public static void main(String[] arg) {
		MyB b1 = new MyB(); //o1
		MyB b2 = new MyB(); //o2
		
		MyB b3 = id(b1);
		MyB b4 = id(b2);
	}	
}
