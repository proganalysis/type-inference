package java.util;
import checkers.inference.reim.quals.*;

public class ArrayDeque<E> extends AbstractCollection<E> implements Deque<E>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public ArrayDeque() { throw new RuntimeException(("skeleton method")); }
  public ArrayDeque(int a1) { throw new RuntimeException(("skeleton method")); }
  public ArrayDeque(@Polyread ArrayDeque<E> this, @Polyread Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public void addFirst(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public void addLast(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean offerFirst(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean offerLast(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public E removeFirst() { throw new RuntimeException(("skeleton method")); }
  public E removeLast() { throw new RuntimeException(("skeleton method")); }
  public E pollFirst() { throw new RuntimeException(("skeleton method")); }
  public E pollLast() { throw new RuntimeException(("skeleton method")); }
  public E getFirst() { throw new RuntimeException(("skeleton method")); }
  public E getLast() { throw new RuntimeException(("skeleton method")); }
  public E peekFirst(@Readonly ArrayDeque<E> this)  { throw new RuntimeException(("skeleton method")); }
  public E peekLast(@Readonly ArrayDeque<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean removeFirstOccurrence(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean removeLastOccurrence(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean add(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean offer(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public E remove() { throw new RuntimeException(("skeleton method")); }
  public E poll() { throw new RuntimeException(("skeleton method")); }
  public E element(@Readonly ArrayDeque<E> this)  { throw new RuntimeException(("skeleton method")); }
  public E peek(@Readonly ArrayDeque<E> this)  { throw new RuntimeException(("skeleton method")); }
  public void push(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public E pop() { throw new RuntimeException(("skeleton method")); }
  public int size(@Readonly ArrayDeque<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean isEmpty(@Readonly ArrayDeque<E> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Iterator<E> iterator(@Polyread ArrayDeque<E> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Iterator<E> descendingIterator(@Polyread ArrayDeque<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean contains(@Readonly ArrayDeque<E> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); } //WEI remove  @I
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public Object[] toArray() { throw new RuntimeException(("skeleton method")); }
  public <T> T[] toArray(T[] a1) { throw new RuntimeException(("skeleton method")); }
  public ArrayDeque<E> clone() { throw new RuntimeException("skeleton method"); }
}
