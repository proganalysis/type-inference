class A {
	char[] arr;
	public A() {
		arr = new char[10];
		arr[0] = 'a';
		init("Ana");
	}
	
	void init(String arg) {
		System.out.println(arg);
		for (int i=0; i<10; i++) {
			System.out.println(arr[i]);
			//arr[i] = 'b';
		}
		//m(arr);
	}
	
	void m(char[] b) {
		b[0]='a';
	}
	
}

public class InitMethod {
	public static void main(String[] arg) {
		A a = new A();
		a.init("Ana");
		//a.m(a.arr);
	}
}
