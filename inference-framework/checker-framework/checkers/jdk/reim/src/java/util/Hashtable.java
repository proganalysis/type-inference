package java.util;
import checkers.inference2.reimN.quals.*;

public class Hashtable<K, V> extends Dictionary<K, V> implements Map<K, V>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public Hashtable(int a1, float a2) { throw new RuntimeException(("skeleton method")); }
  public Hashtable(int a1) { throw new RuntimeException(("skeleton method")); }
  public Hashtable() { throw new RuntimeException(("skeleton method")); }
  public Hashtable(@PolyPoly Hashtable<K, V> this, @PolyPoly Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int size(@ReadRead Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean isEmpty(@ReadRead Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized @PolyPoly Enumeration<K> keys(@PolyPoly Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized @PolyPoly Enumeration<V> elements(@PolyPoly Hashtable<K, V> this) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean contains(@ReadRead Hashtable<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsValue(@ReadRead Hashtable<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean containsKey(@ReadRead Hashtable<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized V get(@PolyPoly Hashtable<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized V put(@ReadRead K a1, @ReadRead V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized V remove(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void putAll(@ReadRead Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void clear() { throw new RuntimeException(("skeleton method")); }
  public synchronized String toString(@ReadRead Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public Set<K> keySet(@PolyPoly Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Set<Map.Entry<K, V>> entrySet(@PolyPoly Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Collection<V> values(@PolyPoly Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean equals(@ReadRead Hashtable<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int hashCode(@ReadRead Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized Object clone() { throw new RuntimeException("skeleton method"); }
}
