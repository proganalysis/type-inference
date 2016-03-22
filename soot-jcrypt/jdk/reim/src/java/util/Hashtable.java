package java.util;
import checkers.inference.reim.quals.*;

public class Hashtable<K, V> extends Dictionary<K, V> implements Map<K, V>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public Hashtable(int a1, float a2) { throw new RuntimeException(("skeleton method")); }
  public Hashtable(int a1) { throw new RuntimeException(("skeleton method")); }
  public Hashtable() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public Hashtable(@Polyread Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized int size()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized boolean isEmpty()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public synchronized @Polyread Enumeration<K> keys()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public synchronized @Polyread Enumeration<V> elements() { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized boolean contains(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean containsValue(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized boolean containsKey(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public synchronized V get(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized V put(@Readonly K a1, @Readonly V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public synchronized V remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void putAll(@Readonly Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public synchronized void clear() { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized String toString()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public Set<K> keySet()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Set<Map.Entry<K, V>> entrySet()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Collection<V> values()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized boolean equals(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public synchronized int hashCode()  { throw new RuntimeException(("skeleton method")); }
  public synchronized Object clone() { throw new RuntimeException("skeleton method"); }
}
