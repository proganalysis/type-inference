package java.util;
import checkers.inference.reim.quals.*;

public interface Enumeration<E> {
    boolean hasMoreElements(@Readonly Enumeration<E> this) ;
    E nextElement(@Polyread Enumeration<E> this) ;
}
