package java.lang;
import checkers.inference.reim.quals.*;
import java.util.*;

public interface Comparable<T> {
    public int compareTo(@Readonly Comparable<T> this, T o) ;
}
