package java.util;
import checkers.inference2.reimN.quals.*;

public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public LinkedList() { throw new RuntimeException(("skeleton method")); }
  public LinkedList(@PolyPoly LinkedList<E> this, @PolyPoly Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public E getFirst(@PolyPoly LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E getLast(@PolyPoly LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E removeFirst() { throw new RuntimeException(("skeleton method")); }
  public E removeLast() { throw new RuntimeException(("skeleton method")); }
  public void addFirst(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public void addLast(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean contains(@ReadRead LinkedList<E> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public int size(@ReadRead LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean add(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(@ReadRead Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(int a1, @ReadRead Collection<? extends E> a2) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public E get(@PolyPoly LinkedList<E> this, int a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E set(int a1, @ReadRead E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public void add(int a1, @ReadRead E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public E remove(int a1) { throw new RuntimeException(("skeleton method")); }
  public int indexOf(@ReadRead LinkedList<E> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public int lastIndexOf(@ReadRead LinkedList<E> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public E peek(@PolyPoly LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E element(@PolyPoly LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E poll() { throw new RuntimeException(("skeleton method")); }
  public E remove() { throw new RuntimeException(("skeleton method")); }
  public boolean offer(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean offerFirst(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean offerLast(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public E peekFirst(@PolyPoly LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); }  //WEI
  public E peekLast(@PolyPoly LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); }  //WEI
  public E pollFirst() { throw new RuntimeException(("skeleton method")); }
  public E pollLast() { throw new RuntimeException(("skeleton method")); }
  public void push(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public E pop() { throw new RuntimeException(("skeleton method")); }
  public boolean removeFirstOccurrence(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean removeLastOccurrence(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly ListIterator<E> listIterator(@PolyPoly LinkedList<E> this, int a1)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Iterator<E> descendingIterator(@PolyPoly LinkedList<E> this)  { throw new RuntimeException(("skeleton method")); }
  public Object[] toArray() { throw new RuntimeException(("skeleton method")); }
  public <T> T[] toArray(T[] a1) { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
