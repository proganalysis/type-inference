package java.util;
import checkers.inference.reim.quals.*;

public interface List<E> extends Collection<E> {
    int size(@Readonly List<E> this) ;
    boolean isEmpty(@Readonly List<E> this) ;
    boolean contains(@Readonly List<E> this, @Readonly Object o) ;
    @Polyread Iterator<E> iterator(@Polyread List<E> this) ;
    @Readonly Object [] toArray(@Readonly List<E> this) ;
    <T> T[] toArray(@Readonly List<E> this, T[] a) ;
    boolean add(@Readonly E e); //WEI
    boolean remove(@Readonly Object o);
    boolean containsAll(@Readonly List<E> this, @Readonly Collection<?> c) ;
    boolean addAll(@Readonly Collection<? extends E> c);
    boolean addAll(int index, @Readonly Collection<? extends E> c);
    boolean removeAll(@Readonly Collection<?> c);
    boolean retainAll(@Readonly Collection<?> c);
    void clear();
    boolean equals(@Readonly List<E> this, @Readonly Object o) ;
    int hashCode(@Readonly List<E> this) ;
    E get(@Polyread List<E> this, int index) ; //WEI
    E set(int index, @Readonly E element); //WEI
    void add(int index, @Readonly E element); //WEI
    E remove(int index);
    int indexOf(@Readonly List<E> this, @Readonly Object o) ;
    int lastIndexOf(@Readonly List<E> this, @Readonly Object o) ;
    @Polyread ListIterator<E> listIterator(@Polyread List<E> this) ;
    @Polyread ListIterator<E> listIterator(@Polyread List<E> this, int index) ;
    @Polyread List<E> subList(@Polyread List<E> this, int fromIndex, int toIndex) ;
}
