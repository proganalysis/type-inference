
public class CFLTests5 {
	
	public void putInArr(String s, String[] arr1) {
		arr1[0] = s;
	}
	/*
	public void putInArr2(String s, String[] arr2) {
		arr2[0] = s;
	}
	*/
	
	public String getFromArr(String[] arr1) {
		return arr1[0];
	}
	/*
	public String getFromArr2(String[] arr2) {
		return arr2[0];
	}
	*/
	
	public static void main(String[] arg) {
		CFLTests5 c = new CFLTests5();
		String[] arr1 = new String[10];
		String[] arr2 = new String[10];
		
		String s1 = new String("Ana");
		String s2 = new String("Antun");
		
		c.putInArr(s1,arr1);
		c.putInArr(s2,arr2);
		
		String s3 = c.getFromArr(arr1);
		String s4 = c.getFromArr(arr2);
		System.out.println(s3+" "+s4);
		
		String[] arr3 = new String[10];
		String s7 = new String("Pooch");
		arr3[0] = s7; 
		String s5 = arr3[0];
		String s6 = s5;
		
	}
}
	