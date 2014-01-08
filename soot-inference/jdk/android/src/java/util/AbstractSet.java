package java.util;
import checkers.inference.reim.quals.*;

public abstract class AbstractSet<E> extends AbstractCollection<E> implements Set<E> {
    protected AbstractSet() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public boolean equals( @Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public int hashCode()  { throw new RuntimeException("skeleton method"); }
    public boolean removeAll(@Readonly Collection<?> c) { throw new RuntimeException("skeleton method"); }

}
