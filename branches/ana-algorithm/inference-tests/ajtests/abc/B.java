import checkers.inference.aj.quals.*;

class A {
  C m() {
    C x;
    x = new C();
    return x;
  }

  A() {}
}

class C {
}

public class B {
  /*@Aliased*/ A a1; 
  C c1;
  A a2;
  public A f() {
    a1 = new A();
    c1 = a1.m();
    a2 = new /*@NonAliased*/ A();
    c1 = a2.m();
    return a2;
  }
}
