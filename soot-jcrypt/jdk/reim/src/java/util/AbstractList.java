package java.util;
import checkers.inference.reim.quals.*;

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
    protected AbstractList() { throw new RuntimeException("skeleton method"); }
    public boolean add(@Readonly E e) { throw new RuntimeException("skeleton method"); } //WEI K
    @PolyreadThis abstract public E get(int index) ; //WEI
    public E set(int index, @Readonly E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public void add(int index, @Readonly E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public E remove(int index) { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public int indexOf(@Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public int lastIndexOf(@Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    public boolean addAll(int index, @Readonly Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread Iterator<E> iterator()  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread ListIterator<E> listIterator()  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread ListIterator<E> listIterator(final int index)  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread List<E> subList(int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public boolean equals(@Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public int hashCode()  { throw new RuntimeException("skeleton method"); }
    protected void removeRange(int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    protected transient int modCount = 0;
}

class SubList<E> extends AbstractList<E> {
    protected SubList() {}
    @PolyreadThis SubList(@Polyread AbstractList<E> list, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
    public E set(int index, @Readonly E element) { throw new RuntimeException("skeleton method"); } //WEI K
    @PolyreadThis public E get(int index)  { throw new RuntimeException("skeleton method"); }  //WEI
    @ReadonlyThis public int size()  { throw new RuntimeException("skeleton method"); }
    public void add(int index, @Readonly E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public E remove(int index) { throw new RuntimeException("skeleton method"); }
    protected void removeRange(int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public boolean addAll(@Readonly Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public boolean addAll(int index, @Readonly Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread Iterator<E> iterator()  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread ListIterator<E> listIterator(final int index)  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread List<E> subList(int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
}

class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {
    @PolyreadThis RandomAccessSubList(@Polyread AbstractList<E> list, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread List<E> subList(int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
}
