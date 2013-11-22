/*>>>
import checkers.inference.sflow.quals.*;
*/

class A {
    B b;
    int m() {
        X x = new X();
        return x.f;
    }
}

class B {}

class X {
    int f;
}

class Main {
    public static void main() {

        A a = new A();
        /*@Tainted*/ int i = a.m();
    }
}

/* Here we'll have a conflict at i = a.m() */

