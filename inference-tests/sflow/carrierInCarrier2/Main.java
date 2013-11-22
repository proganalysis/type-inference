/*>>>
import checkers.inference.sflow.quals.*;
*/

class A {
    B b = new B();
    B b2 = new B(); // this is a Secret B;

    void put(C c, C c2) { b.put(c); b2.put(c2); }
    C get() { return b.get(); }    
    C get2() { return b2.get(); }
}

class B {
    C c;
    void put(C c) { this.c = c; }
    C get() { return this.c; }
}

class C {}

public class Main {
    public static void main() {
	/*@Secret*/ C c = new C();
	A a = new A();
	a.put(null,c);
	A a2 = a;
	/*@Tainted*/ C c2 = a2.get();
	C c3 = a2.get2();
    }
}
/*
  This should type check but let's see.
*/
