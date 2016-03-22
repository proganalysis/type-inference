package java.util;
import checkers.inference.reim.quals.*;

public class Vector<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public Vector(int a1, int a2) { throw new RuntimeException(("skeleton method")); }
  public Vector(int a1) { throw new RuntimeException(("skeleton method")); }
  public Vector() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public Vector(@Polyread Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  // copyInto is special-cased by the type-checker
  @ReadonlyThis public synchronized void copyInto(@Readonly @Mutable Object[] a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized void trimToSize() { throw new RuntimeException(("skeleton method")); }
  public synchronized void ensureCapacity(int a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void setSize(int a1) { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized int capacity()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized int size()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized boolean isEmpty()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public Enumeration<E> elements()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean contains(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int indexOf(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized int indexOf(@Readonly Object a1, int a2)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized int lastIndexOf(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized int lastIndexOf(@Readonly Object a1, int a2)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public synchronized E elementAt(int a1)  { throw new RuntimeException(("skeleton method")); }  // WEI 
  @PolyreadThis public synchronized E firstElement()  { throw new RuntimeException(("skeleton method")); }  // WEI
  @PolyreadThis public synchronized E lastElement()  { throw new RuntimeException(("skeleton method")); } // WEI
  public synchronized void setElementAt(@Readonly E a1, int a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized void removeElementAt(int a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void insertElementAt(@Readonly E a1, int a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized void addElement(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized boolean removeElement(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void removeAllElements() { throw new RuntimeException(("skeleton method")); }
  public synchronized Object[] toArray() { throw new RuntimeException(("skeleton method")); }
  public synchronized <T> T[] toArray(T[] a1) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public synchronized E get(int a1)  { throw new RuntimeException(("skeleton method")); }  // WEI
  public synchronized E set(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized boolean add(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public void add(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized E remove(int a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized boolean containsAll(@Readonly Collection<?> a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean addAll(@Readonly Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean removeAll(@Readonly Collection<?> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean retainAll(@Readonly Collection<?> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean addAll(int a1, @Readonly Collection<? extends E> a2) { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized boolean equals(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized int hashCode()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized String toString()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public synchronized @Polyread List<E> subList(int a1, int a2)  { throw new RuntimeException(("skeleton method")); }
  public synchronized Object clone() { throw new RuntimeException("skeleton method"); }
}
