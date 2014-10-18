package java.util;
import checkers.inference2.reimN.quals.*;

public interface Iterator<E> {
    boolean hasNext(@ReadRead Iterator<E> this) ;
    // For a justification of this annotation, see section
    // "Iterators and their abstract state" in the Checker Framework manual.
    E next(@PolyPoly Iterator<E> this) ;
    void remove();
}
