public class ImmutableArrays {
    char[] a;
    
	ImmutableArrays() {
	   a = new char[10];
	   for (int i=0; i<10; i++) {
		   a[i] = 'a';
	   }
	   //b[0] = 'b';
	}
	
	void m(StringBuffer b) {
		System.out.println(b.toString());
		a[0] = 'a';
		System.out.println(b.toString());
		b.append("ana"); 
	}
	void n() {
		char[] b = a;
		b[1] = 'b';
	}
	
	
	public static void main(String[] args) {
		char[] bb = new char[100];
		bb[5] = 'a';
		ImmutableArrays i = new ImmutableArrays();
		i.m(new StringBuffer("Boza"));
		i.n();
		if (i.a[1] == 'b')
			System.out.println("AHA");
		else
			System.out.println("A-NO");
		// i.a[2] = 'c';
		StringBuffer b = new StringBuffer("Ana");		
		System.out.println(i.a[1]+i.a[0]);
	}
}
