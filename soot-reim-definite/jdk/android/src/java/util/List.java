package java.util;
import checkers.inference.reim.quals.*;

public interface List<E> extends Collection<E> {
    @ReadonlyThis int size() ;
    @ReadonlyThis boolean isEmpty() ;
    @ReadonlyThis boolean contains( @Readonly Object o) ;
    @PolyreadThis @Polyread Iterator<E> iterator() ;
    @ReadonlyThis @Readonly Object [] toArray() ;
    @ReadonlyThis <T> T[] toArray( T[] a) ;
    boolean add(@Readonly E e); //WEI
    boolean remove(@Readonly Object o);
    @ReadonlyThis boolean containsAll( @Readonly Collection<?> c) ;
    boolean addAll(@Readonly Collection<? extends E> c);
    boolean addAll(int index, @Readonly Collection<? extends E> c);
    boolean removeAll(@Readonly Collection<?> c);
    boolean retainAll(@Readonly Collection<?> c);
    void clear();
    @ReadonlyThis boolean equals( @Readonly Object o) ;
    @ReadonlyThis int hashCode() ;
    @PolyreadThis E get( int index) ; //WEI
    E set(int index, @Readonly E element); //WEI
    void add(int index, @Readonly E element); //WEI
    E remove(int index);
    @ReadonlyThis int indexOf( @Readonly Object o) ;
    @ReadonlyThis int lastIndexOf( @Readonly Object o) ;
    @PolyreadThis @Polyread ListIterator<E> listIterator() ;
    @PolyreadThis @Polyread ListIterator<E> listIterator( int index) ;
    @PolyreadThis @Polyread List<E> subList( int fromIndex, int toIndex) ;
}
