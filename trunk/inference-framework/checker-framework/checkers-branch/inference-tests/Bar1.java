public class Bar1 {
    int x;

    public Bar1() /*mutable*/ {
        x = 1;
    }

    public int getX() /*readonly*/ {
        return x;
    }
}
