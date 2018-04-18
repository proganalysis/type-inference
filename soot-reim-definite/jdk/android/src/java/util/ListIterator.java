package java.util;
import checkers.inference.reim.quals.*;

public interface ListIterator<E> extends Iterator<E> {
    @ReadonlyThis boolean hasNext() ;
    @ReadonlyThis E next() ;
    @ReadonlyThis boolean hasPrevious() ;
    @ReadonlyThis E previous() ;
    @ReadonlyThis int nextIndex() ;
    @ReadonlyThis int previousIndex() ;
    void remove();
    void set(E e);
    void add(E e);
}
