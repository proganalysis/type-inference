
public class CFLTests4 {
	String[] arr1 = new String[10];
	String[] arr2 = new String[10];
	/*
	public CFLTests4() {
		arr1 = new String[10];
		arr2 = new String[10];
	}
	*/
	
	public void putInArr1(String s) {
		arr1[0] = s;
	}
	
	public void putInArr2(String s) {
		arr2[0] = s;
	}
	
	public String getFromArr1() {
		return arr1[0];
	}
	
	public String getFromArr2() {
		return arr2[0];
	}
	
	public static void main(String[] arg) {
		CFLTests4 c = new CFLTests4();
		String s1 = new String("Ana");
		String s2 = new String("Antun");
		c.putInArr1(s1);
		c.putInArr2(s2);
		
		String s3 = c.getFromArr1();
		String s4 = c.getFromArr2();
		System.out.println(s3+" "+s4);
	}
}
