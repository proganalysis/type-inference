package java.util;
import checkers.inference2.reimN.quals.*;

public abstract class AbstractCollection<E> implements Collection<E> {
    protected AbstractCollection() { throw new RuntimeException("skeleton method"); }
    public abstract @PolyPoly Iterator<E> iterator(@PolyPoly AbstractCollection<E> this) ;
    public abstract int size(@ReadRead AbstractCollection<E> this) ;
    public boolean isEmpty(@ReadRead AbstractCollection<E> this)  { throw new RuntimeException("skeleton method"); }
    public boolean contains(@ReadRead AbstractCollection<E> this, @ReadRead Object o)  { throw new RuntimeException("skeleton method"); }
    public Object[] toArray(@ReadRead AbstractCollection<E> this)  { throw new RuntimeException("skeleton method"); }
    public <T> T[] toArray(@ReadRead AbstractCollection<E> this, T[] a)  { throw new RuntimeException("skeleton method"); }
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) { throw new RuntimeException("skeleton method"); }
    public boolean add(@ReadRead E e) { throw new RuntimeException("skeleton method"); } //WEI K
    public boolean remove(@ReadRead Object o) { throw new RuntimeException("skeleton method"); }
    public boolean containsAll(@ReadRead AbstractCollection<E> this, @ReadRead Collection<?> c)  { throw new RuntimeException("skeleton method"); }
    public boolean addAll(@ReadRead Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public boolean removeAll(@ReadRead Collection<?> c) { throw new RuntimeException("skeleton method"); }
    public boolean retainAll(@ReadRead Collection<?> c) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    public String toString(@ReadRead AbstractCollection<E> this)  { throw new RuntimeException("skeleton method"); }
}
