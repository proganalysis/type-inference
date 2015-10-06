
class B {
	protected StringBuffer name;
	
	public B(StringBuffer s) {
		name = s;
	}
}

class C extends B {
	
	public C() {
		super(new StringBuffer("Ana"));
	}	
	
	public void set() {
		name.append("Boza");
	}
	public String get() {
		return name.toString();
	}
}

class D {
	C c = new C();
	public void m() {
		c.set();
	}
	
	public String n() {
		return c.get();
	}
}

public class Test1 {
	
	public static void main(String[] arg) {
		D d = new D();
		d.m();
		d.n();
		
	}
	
}
