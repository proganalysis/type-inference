package java.util;
import checkers.inference.reim.quals.*;

public class PriorityQueue<E> extends AbstractQueue<E> implements java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public PriorityQueue() { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(int a1) { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(int a1, Comparator<? super E> a2) { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(@Polyread PriorityQueue<E> this, @Polyread Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(@Polyread PriorityQueue<E> this, @Polyread PriorityQueue<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(@Polyread PriorityQueue<E> this, @Polyread SortedSet<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean add(E a1) { throw new RuntimeException(("skeleton method")); }
  public boolean offer(E a1) { throw new RuntimeException(("skeleton method")); }
  public E peek(@Polyread PriorityQueue<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean contains(@Readonly PriorityQueue<E> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public Object[] toArray() { throw new RuntimeException(("skeleton method")); }
  public <T> T[] toArray(T[] a1) { throw new RuntimeException(("skeleton method")); }
  public @Polyread Iterator<E> iterator(@Polyread PriorityQueue<E> this) { throw new RuntimeException(("skeleton method")); }
  public int size(@Readonly PriorityQueue<E> this)  { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public E poll() { throw new RuntimeException(("skeleton method")); }
  public Comparator<? super E> comparator(@Readonly PriorityQueue<E> this) { throw new RuntimeException(("skeleton method")); }
}
