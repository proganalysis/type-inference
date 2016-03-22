package java.util;
import checkers.inference.reim.quals.*;

public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public TreeSet() { throw new RuntimeException(("skeleton method")); }
  public TreeSet(Comparator<? super E> a1) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public TreeSet(@Polyread Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public TreeSet(@Polyread SortedSet<E> a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Iterator<E> iterator()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Iterator<E> descendingIterator()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread NavigableSet<E> descendingSet()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int size()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean isEmpty()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean contains(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean add(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(@Readonly Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread NavigableSet<E> subSet(E a1, boolean a2, E a3, boolean a4)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread NavigableSet<E> headSet(E a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread NavigableSet<E> tailSet(E a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread SortedSet<E> subSet(E a1, E a2)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread SortedSet<E> headSet(E a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread SortedSet<E> tailSet(E a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public @Polyread Comparator<? super E> comparator()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public E first()  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public E last()  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public E lower(E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public E floor(E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public E ceiling(E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public E higher(E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E pollFirst() { throw new RuntimeException(("skeleton method")); }
  public E pollLast() { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
