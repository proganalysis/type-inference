import java.util.*;

public class Library {

    public void foo() {
        Set<X> set = new HashSet<X>();
        X a1 = new X();
        set.add(a1);
        Set<X> set2 = set;
        int size = set2.size();
        
    }
    
    public static void main(String[] a) {
    	Library l = new Library();
    	l.foo();
    }
}

class X {
    String f;
}
