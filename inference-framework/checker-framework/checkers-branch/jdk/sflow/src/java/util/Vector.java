package java.util;
import checkers.inference.reim.quals.*;

public class Vector<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public Vector(int a1, int a2) { throw new RuntimeException(("skeleton method")); }
  public Vector(int a1) { throw new RuntimeException(("skeleton method")); }
  public Vector() { throw new RuntimeException(("skeleton method")); }
  public Vector(@Polyread Vector<E> this, @Polyread Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  // copyInto is special-cased by the type-checker
  public synchronized void copyInto(@Readonly Vector<E> this, @Readonly Object @Mutable [] a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized void trimToSize() { throw new RuntimeException(("skeleton method")); }
  public synchronized void ensureCapacity(int a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void setSize(int a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized int capacity(@Readonly Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int size(@Readonly Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean isEmpty(@Readonly Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Enumeration<E> elements(@Polyread Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean contains(@Readonly Vector<E> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public int indexOf(@Readonly Vector<E> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int indexOf(@Readonly Vector<E> this, @Readonly Object a1, int a2)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int lastIndexOf(@Readonly Vector<E> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int lastIndexOf(@Readonly Vector<E> this, @Readonly Object a1, int a2)  { throw new RuntimeException(("skeleton method")); }
  public synchronized E elementAt(@Polyread Vector<E> this, int a1)  { throw new RuntimeException(("skeleton method")); }  // WEI 
  public synchronized E firstElement(@Polyread Vector<E> this)  { throw new RuntimeException(("skeleton method")); }  // WEI
  public synchronized E lastElement(@Polyread Vector<E> this)  { throw new RuntimeException(("skeleton method")); } // WEI
  public synchronized void setElementAt(@Readonly E a1, int a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized void removeElementAt(int a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void insertElementAt(@Readonly E a1, int a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized void addElement(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized boolean removeElement(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void removeAllElements() { throw new RuntimeException(("skeleton method")); }
  public synchronized Object[] toArray() { throw new RuntimeException(("skeleton method")); }
  public synchronized <T> T[] toArray(T[] a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized E get(@Polyread Vector<E> this, int a1)  { throw new RuntimeException(("skeleton method")); }  // WEI
  public synchronized E set(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized boolean add(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public void add(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized E remove(int a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean containsAll(@Readonly Vector<E> this, @Readonly Collection<?> a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean addAll(@Readonly Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean removeAll(@Readonly Collection<?> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean retainAll(@Readonly Collection<?> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean addAll(int a1, @Readonly Collection<? extends E> a2) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean equals(@Readonly Vector<E> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int hashCode(@Readonly Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized String toString(@Readonly Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized @Polyread List<E> subList(@Polyread Vector<E> this, int a1, int a2)  { throw new RuntimeException(("skeleton method")); }
  public synchronized Object clone() { throw new RuntimeException("skeleton method"); }
}
