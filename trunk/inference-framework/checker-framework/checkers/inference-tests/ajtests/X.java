import checkers.inference.aj.quals.*;

public class X {
  Contain c;

  public void m() {
    this.c = new Contain();
    Y elem = new Y();
    c.put(0,elem);
    Iter i = c.iter();
    Object elem2 = c.get(0);
  }
}
/*
   class Y {
   Contain d;
   void m() {
   this.d = new Contain();
   d.put(1,1);
   }
   }
   */
class Y {}

class Z {}

class Contain {
  // The following @OwnOwn is for the array
  // If we want to annotate the elements, use @OwnOwn int [] e;
  Y [] e; 
  Z [] d;

  int size;

  // public Object[] getE() {
  //	return e;
  //}

  public Contain() {
    this.e = new Y [10];
    this.d = new Z [10];
  }

  public void put(int i, Y j) {
    d[i] = new Z();
    e[i] = j;
  }

  public Y get(int i) {
    Z x = d[i];
    return e[0];

  }

  public Iter iter() {
    Iter h;
    h = new /*@Aliased*/ Iter(e);
    return h;
  }
}

class Iter {

  Y [] f;

  Iter(Y[] f) {
    // The default annotation for "this" is @OwnPar
    this.f = f;
  }
  public Y next() 
  {
    return f[0];
  }
  /*atomic(L)*/ private transient Object /*@Aliased*/ [] elementData/*this.L[]*/;
}
