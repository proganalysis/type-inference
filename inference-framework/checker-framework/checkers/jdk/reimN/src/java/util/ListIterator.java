package java.util;
import checkers.inference2.reimN.quals.*;

public interface ListIterator<E> extends Iterator<E> {
    boolean hasNext(@ReadRead ListIterator<E> this) ;
    E next(@ReadRead ListIterator<E> this) ;
    boolean hasPrevious(@ReadRead ListIterator<E> this) ;
    E previous(@ReadRead ListIterator<E> this) ;
    int nextIndex(@ReadRead ListIterator<E> this) ;
    int previousIndex(@ReadRead ListIterator<E> this) ;
    void remove();
    void set(E e);
    void add(E e);
}
