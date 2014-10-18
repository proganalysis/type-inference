package java.util;
import checkers.inference2.reimN.quals.*;

public class PriorityQueue<E> extends AbstractQueue<E> implements java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public PriorityQueue() { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(int a1) { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(int a1, Comparator<? super E> a2) { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(@PolyPoly PriorityQueue<E> this, @PolyPoly Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(@PolyPoly PriorityQueue<E> this, @PolyPoly PriorityQueue<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(@PolyPoly PriorityQueue<E> this, @PolyPoly SortedSet<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean add(E a1) { throw new RuntimeException(("skeleton method")); }
  public boolean offer(E a1) { throw new RuntimeException(("skeleton method")); }
  public E peek(@PolyPoly PriorityQueue<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean contains(@ReadRead PriorityQueue<E> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public Object[] toArray() { throw new RuntimeException(("skeleton method")); }
  public <T> T[] toArray(T[] a1) { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Iterator<E> iterator(@PolyPoly PriorityQueue<E> this) { throw new RuntimeException(("skeleton method")); }
  public int size(@ReadRead PriorityQueue<E> this)  { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public E poll() { throw new RuntimeException(("skeleton method")); }
  public Comparator<? super E> comparator(@ReadRead PriorityQueue<E> this) { throw new RuntimeException(("skeleton method")); }
}
