package java.util;
import checkers.inference.reim.quals.*;

public class Stack<E> extends Vector<E> {
    private static final long serialVersionUID = 0L;
    public Stack() { throw new RuntimeException("skeleton method"); }
    public E push(@Readonly E item) { throw new RuntimeException("skeleton method"); } //WEI
    public synchronized E pop() { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public synchronized E peek()  { throw new RuntimeException("skeleton method"); } //WEI
    @ReadonlyThis public boolean empty()  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public synchronized int search(@Readonly Object o)  { throw new RuntimeException("skeleton method"); }
}
