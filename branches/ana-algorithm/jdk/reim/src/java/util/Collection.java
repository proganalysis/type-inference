package java.util;
import checkers.inference.reim.quals.*;

public interface Collection<E> extends Iterable<E> {
    int size(@Readonly Collection<E> this) ;
    boolean isEmpty(@Readonly Collection<E> this) ;
    boolean contains(@Readonly Collection<E> this, @Readonly Object o) ;
    @Polyread Iterator<E> iterator(@Polyread Collection<E> this) ;
    @Readonly Object [] toArray(@Readonly Collection<E> this) ;
    <T> T[] toArray(@Readonly Collection<E> this, T[] a) ;
    boolean add(E e);
    boolean remove(@Readonly Object o);
    boolean containsAll(@Readonly Collection<E> this, @Readonly Collection<?> c) ;
    boolean addAll(@Readonly Collection<? extends E> c);
    boolean removeAll(@Readonly Collection<?> c);
    boolean retainAll(@Readonly Collection<?> c);
    void clear();
    boolean equals(@Readonly Collection<E> this, @Readonly Object o) ;
    int hashCode(@Readonly Collection<E> this) ;
}
