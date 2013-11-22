package java.util;
import checkers.inference.reim.quals.*;

public class TreeMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public TreeMap() { throw new RuntimeException(("skeleton method")); }
  public TreeMap(Comparator<? super K> a1) { throw new RuntimeException(("skeleton method")); }
  public TreeMap(@Polyread TreeMap<K, V> this, @Polyread Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public TreeMap(@Polyread TreeMap<K, V> this, @Polyread SortedMap<K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public int size(@Readonly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsKey(@Readonly TreeMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsValue(@Readonly TreeMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public V get(@Polyread TreeMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public Comparator<? super K> comparator(@Readonly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public K firstKey(@Polyread TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public K lastKey(@Polyread TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public void putAll(@Readonly Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public V put(@Readonly K a1, @Readonly V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public V remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); } // WEI @Readonly was removed
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public @Polyread Map.Entry<K, V> firstEntry(@Polyread TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Map.Entry<K, V> lastEntry(@Polyread TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public Map.Entry<K, V> pollFirstEntry() { throw new RuntimeException(("skeleton method")); }
  public Map.Entry<K, V> pollLastEntry() { throw new RuntimeException(("skeleton method")); }
  public @Polyread Map.Entry<K, V> lowerEntry(@Polyread TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public K lowerKey(@Polyread TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public @Polyread Map.Entry<K, V> floorEntry(@Polyread TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public K floorKey(@Polyread TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public @Polyread Map.Entry<K, V> ceilingEntry(@Polyread TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public K ceilingKey(@Polyread TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public @Polyread Map.Entry<K, V> higherEntry(@Polyread TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public K higherKey(@Polyread TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public Set<K> keySet(@Polyread TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread NavigableSet<K> navigableKeySet(@Polyread TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread NavigableSet<K> descendingKeySet(@Polyread TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Collection<V> values(@Polyread TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Set<Map.Entry<K, V>> entrySet(@Polyread TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread NavigableMap<K, V> descendingMap(@Polyread TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread NavigableMap<K, V> subMap(@Polyread TreeMap<K, V> this, K a1, boolean a2, K a3, boolean a4)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread NavigableMap<K, V> headMap(@Polyread TreeMap<K, V> this, K a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread NavigableMap<K, V> tailMap(@Polyread TreeMap<K, V> this, K a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread SortedMap<K, V> subMap(@Polyread TreeMap<K, V> this, K a1, K a2)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread SortedMap<K, V> headMap(@Polyread TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread SortedMap<K, V> tailMap(@Polyread TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
