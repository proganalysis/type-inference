package java.util;
import checkers.inference.reim.quals.*;

public class Stack<E> extends Vector<E> {
    private static final long serialVersionUID = 0L;
    public Stack() { throw new RuntimeException("skeleton method"); }
    public E push(@Readonly E item) { throw new RuntimeException("skeleton method"); } //WEI
    public synchronized E pop() { throw new RuntimeException("skeleton method"); }
    public synchronized E peek(@Polyread Stack<E> this)  { throw new RuntimeException("skeleton method"); } //WEI
    public boolean empty(@Readonly Stack<E> this)  { throw new RuntimeException("skeleton method"); }
    public synchronized int search(@Readonly Stack<E> this, @Readonly Object o)  { throw new RuntimeException("skeleton method"); }
}
