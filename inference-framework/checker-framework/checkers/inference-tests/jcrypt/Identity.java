import checkers.inference2.jcrypt.quals.*;

class Identity{
	int w, v;
    public void foo(/*Poly*/ Identity this, /*@Sensitive*/ int y) {
    	int x = y;
        int z = id(x);
        w = id(v);
    }
    
    public int id(/*Poly*/ int i) {
    	return i;
    }
} 
