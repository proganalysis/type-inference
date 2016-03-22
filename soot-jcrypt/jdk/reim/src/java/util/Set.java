package java.util;
import checkers.inference.reim.quals.*;

public interface Set<E> extends Collection<E> {
    @ReadonlyThis int size() ;
    @ReadonlyThis boolean isEmpty() ;
    @ReadonlyThis boolean contains(@Readonly Object o) ;
    @PolyreadThis @Polyread Iterator<E> iterator() ;
    @ReadonlyThis @Readonly Object [] toArray() ;
    @ReadonlyThis <T> T[] toArray(T[] a) ;
    boolean add(@Readonly E e); //WEI
    boolean remove(@Readonly Object o);
    @ReadonlyThis boolean containsAll(@Readonly Collection<?> c) ;
    boolean addAll(@Readonly Collection<? extends E> c);
    boolean retainAll(@Readonly Collection<?> c);
    boolean removeAll(@Readonly Collection<?> c);
    void clear();
    @ReadonlyThis boolean equals(@Readonly Object o) ;
    @ReadonlyThis int hashCode() ;
}
