
public class Test2 {

	public static char[] copy(char[] a) {
		char[] r = new char[a.length];
		for (int i=0; i < a.length; i++) {
			r[i] = a[i];
		}
		return r;
	}
	
	public static void main(String[] argc) {
		char[] a = new char[5];
		for (int i=0; i<5; i++)
			a[i] = 'a';
		char[] r = copy(a);
		// System.out.println(r);
		//r[0] = 'a';
	}
	
}
