package java.util;
import checkers.inference2.reimN.quals.*;

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
    protected AbstractList() { throw new RuntimeException("skeleton method"); }
    public boolean add(@ReadRead E e) { throw new RuntimeException("skeleton method"); } //WEI K
    abstract public E get(@PolyPoly AbstractList<E> this, int index) ; //WEI
    public E set(int index, @ReadRead E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public void add(int index, @ReadRead E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public E remove(int index) { throw new RuntimeException("skeleton method"); }
    public int indexOf(@ReadRead AbstractList<E> this, @ReadRead Object o)  { throw new RuntimeException("skeleton method"); }
    public int lastIndexOf(@ReadRead AbstractList<E> this, @ReadRead Object o)  { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    public boolean addAll(int index, @ReadRead Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public @PolyPoly Iterator<E> iterator(@PolyPoly AbstractList<E> this)  { throw new RuntimeException("skeleton method"); }
    public @PolyPoly ListIterator<E> listIterator(@PolyPoly AbstractList<E> this)  { throw new RuntimeException("skeleton method"); }
    public @PolyPoly ListIterator<E> listIterator(@PolyPoly AbstractList<E> this, final int index)  { throw new RuntimeException("skeleton method"); }
    public @PolyPoly List<E> subList(@PolyPoly AbstractList<E> this, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
    public boolean equals(@ReadRead AbstractList<E> this, @ReadRead Object o)  { throw new RuntimeException("skeleton method"); }
    public int hashCode(@ReadRead AbstractList<E> this)  { throw new RuntimeException("skeleton method"); }
    protected void removeRange(int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    protected transient int modCount = 0;
}

class SubList<E> extends AbstractList<E> {
    protected SubList() {}
    SubList(@PolyPoly SubList<E> this, @PolyPoly AbstractList<E> list, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
    public E set(int index, @ReadRead E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public E get(@PolyPoly SubList<E> this, int index)  { throw new RuntimeException("skeleton method"); }  //WEI
    public int size(@ReadRead SubList<E> this)  { throw new RuntimeException("skeleton method"); }
    public void add(int index, @ReadRead E element) { throw new RuntimeException("skeleton method"); } //WEI K
    public E remove(int index) { throw new RuntimeException("skeleton method"); }
    protected void removeRange(int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public boolean addAll(@ReadRead Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public boolean addAll(int index, @ReadRead Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public @PolyPoly Iterator<E> iterator(@PolyPoly SubList<E> this)  { throw new RuntimeException("skeleton method"); }
    public @PolyPoly ListIterator<E> listIterator(@PolyPoly SubList<E> this, final int index)  { throw new RuntimeException("skeleton method"); }
    public @PolyPoly List<E> subList(@PolyPoly SubList<E> this, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
}

class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {
    RandomAccessSubList(@PolyPoly RandomAccessSubList<E> this, @PolyPoly AbstractList<E> list, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
    public @PolyPoly List<E> subList(@PolyPoly RandomAccessSubList<E> this, int fromIndex, int toIndex)  { throw new RuntimeException("skeleton method"); }
}
