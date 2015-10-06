class F {
	char[] arr;
}

class E {
	
	char[] arr;
	void m() {
		arr = new char[5];
		for (int i=0; i<5; i++) {
			arr[i] = 10;
		}
	}
	char[] n() {
		return arr;
	}
	void o(F p) {
		p.arr = arr;
	}
}


public class Test3 {
	public static E create() {
		E e = new E();
		e.m();
		return e;
	}
  public static void main(String[] argc) {
	  E d = create();
	  // E d = new E();
	  // d.m();
	  // char[] b = d.n();
	  F f = new F();
	  d.o(f);
	  char[] b = f.arr;
	  b[0] = 'a';
	  System.out.println(b[0]);
  }
}
