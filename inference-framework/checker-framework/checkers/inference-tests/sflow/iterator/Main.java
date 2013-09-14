/*>>>
import checkers.inference.sflow.quals.*;
*/
class C {
    X arr[] = new X[10];
    void put(X x) { 
        arr[0] = x; 
    }
    X get() { 
        return arr[0]; 
    }
 
    I iter() { 
        I i = new I(this); 
        return i;
    }
}

class I {
    C c;
    I(C c) { 
        this.c = c; 
    }
    
    X next() { 
        X ret = c.arr[0];
        return ret;
    }
}

class X {}

class Main {
    public static void main() {
        /*@Secret*/ X x = new X();
        C c = new C();
        c.put(x);
        I i = c.iter();
        /*@Tainted*/ X x2 = i.next();
    }
}

/* 
   There should be a type error due to the flow x => x2. Look for linear constraints 
   x <: this in C.put
   this <: ret in C.get
   c <: this in I.I
   this <: ret in C.iter
*/
   
