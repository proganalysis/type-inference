import checkers.inference2.jcrypt.quals.*;

class Identity2 {
	
    public int id(int i) {
    	return i;
    }
    
    public int id2(int j) {
    	int t = id(j);
    	return t;
    }
    
    public void foo(/*@Sensitive*/ int b, int d) {
    	int a = id2(b);
    	int c = id2(d);
    }
} 
