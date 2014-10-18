package java.util;
import checkers.inference2.reimN.quals.*;

public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public TreeSet() { throw new RuntimeException(("skeleton method")); }
  public TreeSet(Comparator<? super E> a1) { throw new RuntimeException(("skeleton method")); }
  public TreeSet(@PolyPoly TreeSet<E> this, @PolyPoly Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  public TreeSet(@PolyPoly TreeSet<E> this, @PolyPoly SortedSet<E> a1)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Iterator<E> iterator(@PolyPoly TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Iterator<E> descendingIterator(@PolyPoly TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly NavigableSet<E> descendingSet(@PolyPoly TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public int size(@ReadRead TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean isEmpty(@ReadRead TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean contains(@ReadRead TreeSet<E> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean add(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(@ReadRead Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly NavigableSet<E> subSet(@PolyPoly TreeSet<E> this, E a1, boolean a2, E a3, boolean a4)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly NavigableSet<E> headSet(@PolyPoly TreeSet<E> this, E a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly NavigableSet<E> tailSet(@PolyPoly TreeSet<E> this, E a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly SortedSet<E> subSet(@PolyPoly TreeSet<E> this, E a1, E a2)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly SortedSet<E> headSet(@PolyPoly TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly SortedSet<E> tailSet(@PolyPoly TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Comparator<? super E> comparator(@ReadRead TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); }
  public E first(@PolyPoly TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E last(@PolyPoly TreeSet<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E lower(@PolyPoly TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E floor(@PolyPoly TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E ceiling(@PolyPoly TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E higher(@PolyPoly TreeSet<E> this, E a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E pollFirst() { throw new RuntimeException(("skeleton method")); }
  public E pollLast() { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
