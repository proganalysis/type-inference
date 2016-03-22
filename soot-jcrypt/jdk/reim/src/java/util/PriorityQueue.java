package java.util;
import checkers.inference.reim.quals.*;

public class PriorityQueue<E> extends AbstractQueue<E> implements java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public PriorityQueue() { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(int a1) { throw new RuntimeException(("skeleton method")); }
  public PriorityQueue(int a1, Comparator<? super E> a2) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public PriorityQueue(@Polyread Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public PriorityQueue(@Polyread PriorityQueue<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public PriorityQueue(@Polyread SortedSet<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean add(E a1) { throw new RuntimeException(("skeleton method")); }
  public boolean offer(E a1) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public E peek()  { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean contains(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public Object[] toArray() { throw new RuntimeException(("skeleton method")); }
  public <T> T[] toArray(T[] a1) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Iterator<E> iterator() { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int size()  { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public E poll() { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public Comparator<? super E> comparator() { throw new RuntimeException(("skeleton method")); }
}
