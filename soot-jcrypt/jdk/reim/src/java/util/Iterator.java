package java.util;
import checkers.inference.reim.quals.*;

public interface Iterator<E> {
    @ReadonlyThis boolean hasNext() ;
    // For a justification of this annotation, see section
    // "Iterators and their abstract state" in the Checker Framework manual.
    @PolyreadThis E next() ;
    void remove();
}
