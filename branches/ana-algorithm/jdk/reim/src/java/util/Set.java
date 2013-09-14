package java.util;
import checkers.inference.reim.quals.*;

public interface Set<E> extends Collection<E> {
    int size(@Readonly Set<E> this) ;
    boolean isEmpty(@Readonly Set<E> this) ;
    boolean contains(@Readonly Set<E> this, @Readonly Object o) ;
    @Polyread Iterator<E> iterator(@Polyread Set<E> this) ;
    @Readonly Object [] toArray(@Readonly Set<E> this) ;
    <T> T[] toArray(@Readonly Set<E> this, T[] a) ;
    boolean add(@Readonly E e); //WEI
    boolean remove(@Readonly Object o);
    boolean containsAll(@Readonly Set<E> this, @Readonly Collection<?> c) ;
    boolean addAll(@Readonly Collection<? extends E> c);
    boolean retainAll(@Readonly Collection<?> c);
    boolean removeAll(@Readonly Collection<?> c);
    void clear();
    boolean equals(@Readonly Set<E> this, @Readonly Object o) ;
    int hashCode(@Readonly Set<E> this) ;
}
