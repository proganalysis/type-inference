package java.util;
import checkers.inference2.reimN.quals.*;

public abstract class AbstractSet<E> extends AbstractCollection<E> implements Set<E> {
    protected AbstractSet() { throw new RuntimeException("skeleton method"); }
    public boolean equals(@ReadRead AbstractSet<E> this, @ReadRead Object o)  { throw new RuntimeException("skeleton method"); }
    public int hashCode(@ReadRead AbstractSet<E> this)  { throw new RuntimeException("skeleton method"); }
    public boolean removeAll(@ReadRead Collection<?> c) { throw new RuntimeException("skeleton method"); }

}
