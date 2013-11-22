public class ArraySimple1 {

    public void foo() /*readonly*/ {
        /*readonly*/ Object[/*mutable*/] ma = new Object[1];
        ma[0] = null;
    }

}
