package java.util;
import checkers.inference.reim.quals.*;

public interface ListIterator<E> extends Iterator<E> {
    boolean hasNext(@Readonly ListIterator<E> this) ;
    E next(@Readonly ListIterator<E> this) ;
    boolean hasPrevious(@Readonly ListIterator<E> this) ;
    E previous(@Readonly ListIterator<E> this) ;
    int nextIndex(@Readonly ListIterator<E> this) ;
    int previousIndex(@Readonly ListIterator<E> this) ;
    void remove();
    void set(E e);
    void add(E e);
}
