package java.util;
import checkers.inference.reim.quals.*;

public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public LinkedList() { throw new RuntimeException(("skeleton method")); }
  public LinkedList(@Polyread LinkedList<E> this, @Polyread Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public E getFirst(@Polyread LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E getLast(@Polyread LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E removeFirst() { throw new RuntimeException(("skeleton method")); }
  public E removeLast() { throw new RuntimeException(("skeleton method")); }
  public void addFirst(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public void addLast(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean contains(@Readonly LinkedList<E> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public int size(@Readonly LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean add(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(@Readonly Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(int a1, @Readonly Collection<? extends E> a2) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public E get(@Polyread LinkedList<E> this, int a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E set(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public void add(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public E remove(int a1) { throw new RuntimeException(("skeleton method")); }
  public int indexOf(@Readonly LinkedList<E> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public int lastIndexOf(@Readonly LinkedList<E> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public E peek(@Polyread LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E element(@Polyread LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E poll() { throw new RuntimeException(("skeleton method")); }
  public E remove() { throw new RuntimeException(("skeleton method")); }
  public boolean offer(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean offerFirst(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean offerLast(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public E peekFirst(@Polyread LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); }  //WEI
  public E peekLast(@Polyread LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); }  //WEI
  public E pollFirst() { throw new RuntimeException(("skeleton method")); }
  public E pollLast() { throw new RuntimeException(("skeleton method")); }
  public void push(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public E pop() { throw new RuntimeException(("skeleton method")); }
  public boolean removeFirstOccurrence(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean removeLastOccurrence(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public @Polyread ListIterator<E> listIterator(@Polyread LinkedList<E> this, int a1)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Iterator<E> descendingIterator(@Polyread LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); }
  public Object[] toArray() { throw new RuntimeException(("skeleton method")); }
  public <T> T[] toArray(T[] a1) { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
