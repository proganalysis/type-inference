package java.util;
import checkers.inference.reim.quals.*;

public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public TreeSet() { throw new RuntimeException(("skeleton method")); }
  public TreeSet(Comparator<? super E> a1) { throw new RuntimeException(("skeleton method")); }
  public TreeSet(@Polyread TreeSet<E> this, @Polyread Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public TreeSet(@Polyread TreeSet<E> this, @Polyread SortedSet<E> a1)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Iterator<E> iterator(@Polyread TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Iterator<E> descendingIterator(@Polyread TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread NavigableSet<E> descendingSet(@Polyread TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public int size(@Readonly TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean isEmpty(@Readonly TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean contains(@Readonly TreeSet<E> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean add(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(@Readonly Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
  public @Polyread NavigableSet<E> subSet(@Polyread TreeSet<E> this, E a1, boolean a2, E a3, boolean a4)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread NavigableSet<E> headSet(@Polyread TreeSet<E> this, E a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread NavigableSet<E> tailSet(@Polyread TreeSet<E> this, E a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread SortedSet<E> subSet(@Polyread TreeSet<E> this, E a1, E a2)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread SortedSet<E> headSet(@Polyread TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread SortedSet<E> tailSet(@Polyread TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Comparator<? super E> comparator(@Readonly TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public E first(@Polyread TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E last(@Polyread TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E lower(@Polyread TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E floor(@Polyread TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E ceiling(@Polyread TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E higher(@Polyread TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E pollFirst() { throw new RuntimeException(("skeleton method")); }
  public E pollLast() { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
