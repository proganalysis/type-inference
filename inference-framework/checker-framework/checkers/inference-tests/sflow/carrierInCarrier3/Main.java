/*>>>
import checkers.inference.sflow.quals.*;
*/
class A {
    B b = new B();
    B b2 = new B();

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
	a.put(c,null);
	/*@Tainted*/ C c2 = a.get2(); // This forces A.b2 to be poly.

	
	/*@Secret*/ C c3 = new C();
	A a2 = new A();
	a2.put(null,c3);
	/*@Tainted*/ C c4 = a2.get(); // This forces A.b to be poly. 
    }
}
/*
  This should give a type error due to imprecision. 
  Look for linear constraints c <: this and c2 <: this in A.put().
*/
