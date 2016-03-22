package java.util;
import checkers.inference.reim.quals.*;

public class HashSet<E>
    extends AbstractSet<E>
    implements Set<E>, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 0L;
    public HashSet() { throw new RuntimeException("skeleton method"); }
    public HashSet(@Readonly Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public HashSet(int initialCapacity, float loadFactor) { throw new RuntimeException("skeleton method"); }
    public HashSet(int initialCapacity) { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread Iterator<E> iterator()  { throw new RuntimeException("skeleton method"); }
    public int size() { throw new RuntimeException("skeleton method"); }
    public boolean isEmpty() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public boolean contains(@Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    public boolean add(E e) { throw new RuntimeException("skeleton method"); }
    public boolean remove(@Readonly Object o) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public Object clone()  { throw new RuntimeException("skeleton method"); }
}
