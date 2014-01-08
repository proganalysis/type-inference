import java.util.*;

public class Library {

    public void foo() {
        Set<X> set = new HashSet<X>();
        X a1 = new X();
        set.add(a1);
        Set<X> set2 = set;
        int size = set2.size();
    }
}

class X {
    String f;
}
