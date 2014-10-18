package java.util;
import checkers.inference2.reimN.quals.*;

public class Stack<E> extends Vector<E> {
    private static final long serialVersionUID = 0L;
    public Stack() { throw new RuntimeException("skeleton method"); }
    public E push(@ReadRead E item) { throw new RuntimeException("skeleton method"); } //WEI
    public synchronized E pop() { throw new RuntimeException("skeleton method"); }
    public synchronized E peek(@PolyPoly Stack<E> this)  { throw new RuntimeException("skeleton method"); } //WEI
    public boolean empty(@ReadRead Stack<E> this)  { throw new RuntimeException("skeleton method"); }
    public synchronized int search(@ReadRead Stack<E> this, @ReadRead Object o)  { throw new RuntimeException("skeleton method"); }
}
