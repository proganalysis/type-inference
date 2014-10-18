package java.util;
import checkers.inference2.reimN.quals.*;

public interface List<E> extends Collection<E> {
    int size(@ReadRead List<E> this) ;
    boolean isEmpty(@ReadRead List<E> this) ;
    boolean contains(@ReadRead List<E> this, @ReadRead Object o) ;
    @PolyPoly Iterator<E> iterator(@PolyPoly List<E> this) ;
    @ReadRead Object [] toArray(@ReadRead List<E> this) ;
    <T> T[] toArray(@ReadRead List<E> this, T[] a) ;
    boolean add(@ReadRead E e); //WEI
    boolean remove(@ReadRead Object o);
    boolean containsAll(@ReadRead List<E> this, @ReadRead Collection<?> c) ;
    boolean addAll(@ReadRead Collection<? extends E> c);
    boolean addAll(int index, @ReadRead Collection<? extends E> c);
    boolean removeAll(@ReadRead Collection<?> c);
    boolean retainAll(@ReadRead Collection<?> c);
    void clear();
    boolean equals(@ReadRead List<E> this, @ReadRead Object o) ;
    int hashCode(@ReadRead List<E> this) ;
    E get(@PolyPoly List<E> this, int index) ; //WEI
    E set(int index, @ReadRead E element); //WEI
    void add(int index, @ReadRead E element); //WEI
    E remove(int index);
    int indexOf(@ReadRead List<E> this, @ReadRead Object o) ;
    int lastIndexOf(@ReadRead List<E> this, @ReadRead Object o) ;
    @PolyPoly ListIterator<E> listIterator(@PolyPoly List<E> this) ;
    @PolyPoly ListIterator<E> listIterator(@PolyPoly List<E> this, int index) ;
    @PolyPoly List<E> subList(@PolyPoly List<E> this, int fromIndex, int toIndex) ;
}
