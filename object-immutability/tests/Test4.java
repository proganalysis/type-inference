class BB {
	int i;
}

class AA {
	char[] arr;
	BB f;
	AA() {
		arr = new char[2];
		f = new BB();
	}
	
	BB get() { return f; }
	
	char[] getArray() { return arr; }
	
	static AA createInstance() {
		return new AA();
	}
	
	
 }


public class Test4 {
	
	BB f;
	
	BB m() {
		AA a = AA.createInstance();
		BB bb = a.get();
		f = bb;
		return bb;
	}
	
	void n() {
		f.i = 0;
	}
	
	static Test4 create() {
		return new Test4();
	}
	
	public static void main(String[] arg) {
		Test4 t = create();
		BB bb = t.m();
		t.n();
		// bb.i = 0;
	}
}
