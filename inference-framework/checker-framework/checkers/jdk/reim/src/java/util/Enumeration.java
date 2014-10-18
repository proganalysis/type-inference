package java.util;
import checkers.inference2.reimN.quals.*;

public interface Enumeration<E> {
    boolean hasMoreElements(@ReadRead Enumeration<E> this) ;
    E nextElement(@ReadRead Enumeration<E> this) ;
}
