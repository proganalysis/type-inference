package java.util;
import checkers.inference2.reimN.quals.*;

public interface Collection<E> extends Iterable<E> {
    int size(@ReadRead Collection<E> this) ;
    boolean isEmpty(@ReadRead Collection<E> this) ;
    boolean contains(@ReadRead Collection<E> this, @ReadRead Object o) ;
    @PolyPoly Iterator<E> iterator(@PolyPoly Collection<E> this) ;
    @ReadRead Object [] toArray(@ReadRead Collection<E> this) ;
    <T> T[] toArray(@ReadRead Collection<E> this, T[] a) ;
    boolean add(E e);
    boolean remove(@ReadRead Object o);
    boolean containsAll(@ReadRead Collection<E> this, @ReadRead Collection<?> c) ;
    boolean addAll(@ReadRead Collection<? extends E> c);
    boolean removeAll(@ReadRead Collection<?> c);
    boolean retainAll(@ReadRead Collection<?> c);
    void clear();
    boolean equals(@ReadRead Collection<E> this, @ReadRead Object o) ;
    int hashCode(@ReadRead Collection<E> this) ;
}
