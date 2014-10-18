package java.util;
import checkers.inference2.reimN.quals.*;

public interface Deque<E> extends Queue<E> {
  public abstract void addFirst(@ReadRead E a1); //WEI
  public abstract void addLast(@ReadRead E a1);//WEI
  public abstract boolean offerFirst(E a1);
  public abstract boolean offerLast(E a1);
  public abstract E removeFirst();
  public abstract E removeLast();
  public abstract E pollFirst();
  public abstract E pollLast();
  public abstract E getFirst(@PolyPoly Deque<E> this) ; //WEI
  public abstract E getLast(@PolyPoly Deque<E> this)  ; //WEI
  public abstract E peekFirst(@PolyPoly Deque<E> this) ; //WEI
  public abstract E peekLast(@PolyPoly Deque<E> this) ;//WEI
  public abstract boolean removeFirstOccurrence(@ReadRead Object a1);
  public abstract boolean removeLastOccurrence(@ReadRead Object a1);
  public abstract boolean add(@ReadRead E a1); //WEI
  public abstract boolean offer(@ReadRead E a1); //WEI
  public abstract E remove();
  public abstract E poll();
  public abstract E element();
  public abstract E peek(@ReadRead Deque<E> this) ;
  public abstract void push(@ReadRead E a1); //WEI
  public abstract E pop();
  public abstract boolean remove(@ReadRead Object a1);
  public abstract boolean contains(@ReadRead Deque<E> this, @ReadRead Object a1) ;
  public abstract int size(@ReadRead Deque<E> this) ;
  public abstract @PolyPoly Iterator<E> iterator(@PolyPoly Deque<E> this) ;
  public abstract @PolyPoly Iterator<E> descendingIterator(@PolyPoly Deque<E> this) ;
}
