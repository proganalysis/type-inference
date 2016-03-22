package java.util;
import checkers.inference.reim.quals.*;

public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public LinkedList() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public LinkedList(@Polyread Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public E getFirst()  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public E getLast()  { throw new RuntimeException(("skeleton method")); } //WEI
  public E removeFirst() { throw new RuntimeException(("skeleton method")); }
  public E removeLast() { throw new RuntimeException(("skeleton method")); }
  public void addFirst(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public void addLast(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  @ReadonlyThis public boolean contains(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int size()  { throw new RuntimeException(("skeleton method")); }
  public boolean add(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(@Readonly Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(int a1, @Readonly Collection<? extends E> a2) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public E get(int a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E set(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public void add(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public E remove(int a1) { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int indexOf(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int lastIndexOf(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public E peek()  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public E element()  { throw new RuntimeException(("skeleton method")); } //WEI
  public E poll() { throw new RuntimeException(("skeleton method")); }
  public E remove() { throw new RuntimeException(("skeleton method")); }
  public boolean offer(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean offerFirst(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean offerLast(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public E peekFirst()  { throw new RuntimeException(("skeleton method")); }  //WEI
  @PolyreadThis public E peekLast()  { throw new RuntimeException(("skeleton method")); }  //WEI
  public E pollFirst() { throw new RuntimeException(("skeleton method")); }
  public E pollLast() { throw new RuntimeException(("skeleton method")); }
  public void push(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public E pop() { throw new RuntimeException(("skeleton method")); }
  public boolean removeFirstOccurrence(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean removeLastOccurrence(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread ListIterator<E> listIterator(int a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Iterator<E> descendingIterator()  { throw new RuntimeException(("skeleton method")); }
  public Object[] toArray() { throw new RuntimeException(("skeleton method")); }
  public <T> T[] toArray(T[] a1) { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
