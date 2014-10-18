package java.util;
import checkers.inference.reim.quals.*;

public interface Deque<E> extends Queue<E> {
  public abstract void addFirst(@Readonly E a1); //WEI
  public abstract void addLast(@Readonly E a1);//WEI
  public abstract boolean offerFirst(E a1);
  public abstract boolean offerLast(E a1);
  public abstract E removeFirst();
  public abstract E removeLast();
  public abstract E pollFirst();
  public abstract E pollLast();
  public abstract E getFirst(@Polyread Deque<E> this) ; //WEI
  public abstract E getLast(@Polyread Deque<E> this)  ; //WEI
  public abstract E peekFirst(@Polyread Deque<E> this) ; //WEI
  public abstract E peekLast(@Polyread Deque<E> this) ;//WEI
  public abstract boolean removeFirstOccurrence(@Readonly Object a1);
  public abstract boolean removeLastOccurrence(@Readonly Object a1);
  public abstract boolean add(@Readonly E a1); //WEI
  public abstract boolean offer(@Readonly E a1); //WEI
  public abstract E remove();
  public abstract E poll();
  public abstract E element();
  public abstract E peek(@Readonly Deque<E> this) ;
  public abstract void push(@Readonly E a1); //WEI
  public abstract E pop();
  public abstract boolean remove(@Readonly Object a1);
  public abstract boolean contains(@Readonly Deque<E> this, @Readonly Object a1) ;
  public abstract int size(@Readonly Deque<E> this) ;
  public abstract @Polyread Iterator<E> iterator(@Polyread Deque<E> this) ;
  public abstract @Polyread Iterator<E> descendingIterator(@Polyread Deque<E> this) ;
}
