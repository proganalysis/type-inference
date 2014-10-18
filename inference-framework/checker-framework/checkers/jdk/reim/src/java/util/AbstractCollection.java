package java.util;
import checkers.inference.reim.quals.*;

public abstract class AbstractCollection<E> implements Collection<E> {
    protected AbstractCollection() { throw new RuntimeException("skeleton method"); }
    public abstract @Polyread Iterator<E> iterator(@Polyread AbstractCollection<E> this) ;
    public abstract int size(@Readonly AbstractCollection<E> this) ;
    public boolean isEmpty(@Readonly AbstractCollection<E> this)  { throw new RuntimeException("skeleton method"); }
    public boolean contains(@Readonly AbstractCollection<E> this, @Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    public Object[] toArray(@Readonly AbstractCollection<E> this)  { throw new RuntimeException("skeleton method"); }
    public <T> T[] toArray(@Readonly AbstractCollection<E> this, T[] a)  { throw new RuntimeException("skeleton method"); }
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) { throw new RuntimeException("skeleton method"); }
    public boolean add(@Readonly E e) { throw new RuntimeException("skeleton method"); } //WEI K
    public boolean remove(@Readonly Object o) { throw new RuntimeException("skeleton method"); }
    public boolean containsAll(@Readonly AbstractCollection<E> this, @Readonly Collection<?> c)  { throw new RuntimeException("skeleton method"); }
    public boolean addAll(@Readonly Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public boolean removeAll(@Readonly Collection<?> c) { throw new RuntimeException("skeleton method"); }
    public boolean retainAll(@Readonly Collection<?> c) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    public String toString(@Readonly AbstractCollection<E> this)  { throw new RuntimeException("skeleton method"); }
}
