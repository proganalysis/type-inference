package java.util;
import checkers.inference2.reimN.quals.*;

public class HashSet<E>
    extends AbstractSet<E>
    implements Set<E>, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 0L;
    public HashSet() { throw new RuntimeException("skeleton method"); }
    public HashSet(@ReadRead Collection<? extends E> c) { throw new RuntimeException("skeleton method"); }
    public HashSet(int initialCapacity, float loadFactor) { throw new RuntimeException("skeleton method"); }
    public HashSet(int initialCapacity) { throw new RuntimeException("skeleton method"); }
    public @PolyPoly Iterator<E> iterator(@PolyPoly HashSet<E> this)  { throw new RuntimeException("skeleton method"); }
    public int size() { throw new RuntimeException("skeleton method"); }
    public boolean isEmpty() { throw new RuntimeException("skeleton method"); }
    public boolean contains(@ReadRead HashSet<E> this, @ReadRead Object o)  { throw new RuntimeException("skeleton method"); }
    public boolean add(E e) { throw new RuntimeException("skeleton method"); }
    public boolean remove(@ReadRead Object o) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    public Object clone(@ReadRead HashSet<E> this)  { throw new RuntimeException("skeleton method"); }
}
