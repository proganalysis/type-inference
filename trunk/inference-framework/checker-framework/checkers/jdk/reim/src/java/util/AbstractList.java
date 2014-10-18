package java.util;
import checkers.inference.reim.quals.*;

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
    protected AbstractList() { throw new RuntimeException("skeleton method"); }
    public boolean add(@Readonly E e) { throw new RuntimeException("skeleton method"); } //WEI K
    abstract public E get(@Polyread AbstractList<E> this, int index) ; //WEI
    public E set(int index, @Readonly E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public void add(int index, @Readonly E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public E remove(int index) { throw new RuntimeException("skeleton method"); }
    public int indexOf(@Readonly AbstractList<E> this, @Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    public int lastIndexOf(@Readonly AbstractList<E> this, @Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    public boolean addAll(int index, @Readonly Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public @Polyread Iterator<E> iterator(@Polyread AbstractList<E> this)  { throw new RuntimeException("skeleton method"); }
    public @Polyread ListIterator<E> listIterator(@Polyread AbstractList<E> this)  { throw new RuntimeException("skeleton method"); }
    public @Polyread ListIterator<E> listIterator(@Polyread AbstractList<E> this, final int index)  { throw new RuntimeException("skeleton method"); }
    public @Polyread List<E> subList(@Polyread AbstractList<E> this, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
    public boolean equals(@Readonly AbstractList<E> this, @Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    public int hashCode(@Readonly AbstractList<E> this)  { throw new RuntimeException("skeleton method"); }
    protected void removeRange(int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    protected transient int modCount = 0;
}

class SubList<E> extends AbstractList<E> {
    protected SubList() {}
    SubList(@Polyread SubList<E> this, @Polyread AbstractList<E> list, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
    public E set(int index, @Readonly E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public E get(@Polyread SubList<E> this, int index)  { throw new RuntimeException("skeleton method"); }  //WEI
    public int size(@Readonly SubList<E> this)  { throw new RuntimeException("skeleton method"); }
    public void add(int index, @Readonly E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public E remove(int index) { throw new RuntimeException("skeleton method"); }
    protected void removeRange(int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public boolean addAll(@Readonly Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public boolean addAll(int index, @Readonly Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public @Polyread Iterator<E> iterator(@Polyread SubList<E> this)  { throw new RuntimeException("skeleton method"); }
    public @Polyread ListIterator<E> listIterator(@Polyread SubList<E> this, final int index)  { throw new RuntimeException("skeleton method"); }
    public @Polyread List<E> subList(@Polyread SubList<E> this, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
}

class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {
    RandomAccessSubList(@Polyread RandomAccessSubList<E> this, @Polyread AbstractList<E> list, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
    public @Polyread List<E> subList(@Polyread RandomAccessSubList<E> this, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
}
