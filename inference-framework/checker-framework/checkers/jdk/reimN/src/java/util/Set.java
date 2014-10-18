package java.util;
import checkers.inference2.reimN.quals.*;

public interface Set<E> extends Collection<E> {
    int size(@ReadRead Set<E> this) ;
    boolean isEmpty(@ReadRead Set<E> this) ;
    boolean contains(@ReadRead Set<E> this, @ReadRead Object o) ;
    @PolyPoly Iterator<E> iterator(@PolyPoly Set<E> this) ;
    @ReadRead Object [] toArray(@ReadRead Set<E> this) ;
    <T> T[] toArray(@ReadRead Set<E> this, T[] a) ;
    boolean add(@ReadRead E e); //WEI
    boolean remove(@ReadRead Object o);
    boolean containsAll(@ReadRead Set<E> this, @ReadRead Collection<?> c) ;
    boolean addAll(@ReadRead Collection<? extends E> c);
    boolean retainAll(@ReadRead Collection<?> c);
    boolean removeAll(@ReadRead Collection<?> c);
    void clear();
    boolean equals(@ReadRead Set<E> this, @ReadRead Object o) ;
    int hashCode(@ReadRead Set<E> this) ;
}
