/*>>>
import checkers.inference.sflow.quals.*;
*/
class A {
    B b = new B();
    void put(C c) { b.put(c); }
    C get() { return b.get(); }
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
	a.put(c);
	A a2 = a;
	/*@Tainted*/ C c2 = a2.get();
	
    }
}
/*
  Check that we have linear constraints c <: this for A.put
  and this <: ret for A.get.
  There should be type error as we can't have the Secret
  c flow to the Tainted c2.
*/
