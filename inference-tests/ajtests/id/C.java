import checkers.inference.aj.quals.*;

class Id {
  Id id() {
    Id x;
    x = this;
    return x;
  }
}

class E extends Id {}

public class C {
  public Id m() {
    E y;
    Id z;
    y = new /*@Aliased*/ E();
    z = y.id();
    return z;
  }
}
