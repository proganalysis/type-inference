package java.lang;
import checkers.inference.reim.quals.*;
import java.util.*;

public interface Comparable<T> {
    @ReadonlyThis public int compareTo( T o) ;
}
