package java.util;
import checkers.inference2.reimN.quals.*;

public class Vector<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public Vector(int a1, int a2) { throw new RuntimeException(("skeleton method")); }
  public Vector(int a1) { throw new RuntimeException(("skeleton method")); }
  public Vector() { throw new RuntimeException(("skeleton method")); }
  public Vector(@PolyPoly Vector<E> this, @PolyPoly Collection<? extends E> a1)  { throw new RuntimeException(("skeleton method")); }
  // copyInto is special-cased by the type-checker
  public synchronized void copyInto(@ReadRead Vector<E> this, @ReadRead Object @MutMut [] a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized void trimToSize() { throw new RuntimeException(("skeleton method")); }
  public synchronized void ensureCapacity(int a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void setSize(int a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized int capacity(@ReadRead Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int size(@ReadRead Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean isEmpty(@ReadRead Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public Enumeration<E> elements(@PolyPoly Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean contains(@ReadRead Vector<E> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public int indexOf(@ReadRead Vector<E> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int indexOf(@ReadRead Vector<E> this, @ReadRead Object a1, int a2)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int lastIndexOf(@ReadRead Vector<E> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int lastIndexOf(@ReadRead Vector<E> this, @ReadRead Object a1, int a2)  { throw new RuntimeException(("skeleton method")); }
  public synchronized E elementAt(@PolyPoly Vector<E> this, int a1)  { throw new RuntimeException(("skeleton method")); }  // WEI 
  public synchronized E firstElement(@PolyPoly Vector<E> this)  { throw new RuntimeException(("skeleton method")); }  // WEI
  public synchronized E lastElement(@PolyPoly Vector<E> this)  { throw new RuntimeException(("skeleton method")); } // WEI
  public synchronized void setElementAt(@ReadRead E a1, int a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized void removeElementAt(int a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void insertElementAt(@ReadRead E a1, int a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized void addElement(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized boolean removeElement(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void removeAllElements() { throw new RuntimeException(("skeleton method")); }
  public synchronized Object[] toArray() { throw new RuntimeException(("skeleton method")); }
  public synchronized <T> T[] toArray(T[] a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized E get(@PolyPoly Vector<E> this, int a1)  { throw new RuntimeException(("skeleton method")); }  // WEI
  public synchronized E set(int a1, @ReadRead E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized boolean add(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean remove(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public void add(int a1, @ReadRead E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized E remove(int a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean containsAll(@ReadRead Vector<E> this, @ReadRead Collection<?> a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean addAll(@ReadRead Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean removeAll(@ReadRead Collection<?> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean retainAll(@ReadRead Collection<?> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean addAll(int a1, @ReadRead Collection<? extends E> a2) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean equals(@ReadRead Vector<E> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int hashCode(@ReadRead Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized String toString(@ReadRead Vector<E> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized @PolyPoly List<E> subList(@PolyPoly Vector<E> this, int a1, int a2)  { throw new RuntimeException(("skeleton method")); }
  public synchronized Object clone() { throw new RuntimeException("skeleton method"); }
}
