package java.util;
import checkers.inference.reim.quals.*;

public interface Iterator<E> {
    boolean hasNext(@Readonly Iterator<E> this) ;
    // For a justification of this annotation, see section
    // "Iterators and their abstract state" in the Checker Framework manual.
    E next(@Polyread Iterator<E> this) ;
    void remove();
}
