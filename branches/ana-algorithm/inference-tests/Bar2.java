public class Bar2 {
    /*readonly*/ Integer x;

    public Bar2() /*mutable*/ {
        x = null;
    }

    public int foo() /*readonly*/ {
        return x.intValue();
    }

    // readonly because nowhere is the return type of fooChange used in a
    // mutable manner.
    public /*readonly*/ Integer fooChange(/*readonly*/ Integer i) /*mutable*/ {
        x = i;
        return i;
    }

    public /*readonly*/ Integer fooReadOnly() /*readonly*/ {
        return x;
    }

    public /*readonly*/ Integer fooNull() /*mutable*/ {
        x = null;
        return x;
    }
}
