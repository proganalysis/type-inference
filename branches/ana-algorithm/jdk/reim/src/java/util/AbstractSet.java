package java.util;
import checkers.inference.reim.quals.*;

public abstract class AbstractSet<E> extends AbstractCollection<E> implements Set<E> {
    protected AbstractSet() { throw new RuntimeException("skeleton method"); }
    public boolean equals(@Readonly AbstractSet<E> this, @Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    public int hashCode(@Readonly AbstractSet<E> this)  { throw new RuntimeException("skeleton method"); }
    public boolean removeAll(@Readonly Collection<?> c) { throw new RuntimeException("skeleton method"); }

}
