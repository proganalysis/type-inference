package java.util;
import checkers.inference.reim.quals.*;

public interface Enumeration<E> {
    @ReadonlyThis boolean hasMoreElements() ;
    @ReadonlyThis E nextElement() ;
}
