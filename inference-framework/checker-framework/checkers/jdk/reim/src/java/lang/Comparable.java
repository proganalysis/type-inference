package java.lang;
import checkers.inference2.reimN.quals.*;
import java.util.*;

public interface Comparable<T> {
    public int compareTo(@PolyPoly Comparable<T> this, @PolyPoly T o) ;
}
