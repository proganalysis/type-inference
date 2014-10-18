package java.util;
import checkers.inference.reim.quals.*;

public class Hashtable<K, V> extends Dictionary<K, V> implements Map<K, V>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public Hashtable(int a1, float a2) { throw new RuntimeException(("skeleton method")); }
  public Hashtable(int a1) { throw new RuntimeException(("skeleton method")); }
  public Hashtable() { throw new RuntimeException(("skeleton method")); }
  public Hashtable(@Polyread Hashtable<K, V> this, @Polyread Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int size(@Readonly Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean isEmpty(@Readonly Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized @Polyread Enumeration<K> keys(@Polyread Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized @Polyread Enumeration<V> elements(@Polyread Hashtable<K, V> this) { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean contains(@Readonly Hashtable<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsValue(@Readonly Hashtable<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean containsKey(@Readonly Hashtable<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized V get(@Polyread Hashtable<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized V put(@Readonly K a1, @Readonly V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized V remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void putAll(@Readonly Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void clear() { throw new RuntimeException(("skeleton method")); }
  public synchronized String toString(@Readonly Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Set<K> keySet(@Polyread Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Set<Map.Entry<K, V>> entrySet(@Polyread Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Collection<V> values(@Polyread Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized boolean equals(@Readonly Hashtable<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public synchronized int hashCode(@Readonly Hashtable<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public synchronized Object clone() { throw new RuntimeException("skeleton method"); }
}
