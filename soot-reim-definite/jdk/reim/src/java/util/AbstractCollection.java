package java.util;
import checkers.inference.reim.quals.*;

public abstract class AbstractCollection<E> implements Collection<E> {
    protected AbstractCollection() { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public abstract @Polyread Iterator<E> iterator() ;
    @ReadonlyThis public abstract int size() ;
    @ReadonlyThis public boolean isEmpty()  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public boolean contains( @Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public Object[] toArray()  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public <T> T[] toArray( T[] a)  { throw new RuntimeException("skeleton method"); }
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) { throw new RuntimeException("skeleton method"); }
    public boolean add(@Readonly E e) { throw new RuntimeException("skeleton method"); } //WEI K
    public boolean remove(@Readonly Object o) { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public boolean containsAll( @Readonly Collection<?> c)  { throw new RuntimeException("skeleton method"); }
    public boolean addAll(@Readonly Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public boolean removeAll(@Readonly Collection<?> c) { throw new RuntimeException("skeleton method"); }
    public boolean retainAll(@Readonly Collection<?> c) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public String toString()  { throw new RuntimeException("skeleton method"); }
}
