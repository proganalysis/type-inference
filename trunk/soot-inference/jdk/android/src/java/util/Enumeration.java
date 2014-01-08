package java.util;
import checkers.inference.reim.quals.*;

public interface Enumeration<E> {
    @ReadonlyThis boolean hasMoreElements() ;
    @PolyreadThis E nextElement() ;
}
