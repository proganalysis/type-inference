import checkers.inference2.reimN.quals.*;

public class Bar1 {
    int g;

    public /*ReadMut*/ Bar1() /*mutable*/ {
        g = 1;
    }

    public int getX() /*readonly*/ {
        return g;
    }
}
