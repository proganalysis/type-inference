package java.util;
import checkers.inference.reim.quals.*;

public class TreeMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public TreeMap() { throw new RuntimeException(("skeleton method")); }
  public TreeMap(Comparator<? super K> a1) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public TreeMap( @Polyread Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public TreeMap( @Polyread SortedMap<K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int size()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean containsKey( @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean containsValue( @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public V get( @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  @ReadonlyThis public Comparator<? super K> comparator()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public K firstKey()  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public K lastKey()  { throw new RuntimeException(("skeleton method")); } //WEI
  public void putAll(@Readonly Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public V put(@Readonly K a1, @Readonly V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public V remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); } // WEI @Readonly was removed
  public void clear() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Map.Entry<K, V> firstEntry()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Map.Entry<K, V> lastEntry()  { throw new RuntimeException(("skeleton method")); }
  public Map.Entry<K, V> pollFirstEntry() { throw new RuntimeException(("skeleton method")); }
  public Map.Entry<K, V> pollLastEntry() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Map.Entry<K, V> lowerEntry( K a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public K lowerKey( K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public @Polyread Map.Entry<K, V> floorEntry( K a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public K floorKey( K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public @Polyread Map.Entry<K, V> ceilingEntry( K a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public K ceilingKey( K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public @Polyread Map.Entry<K, V> higherEntry( K a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public K higherKey( K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  @PolyreadThis public Set<K> keySet()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread NavigableSet<K> navigableKeySet()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread NavigableSet<K> descendingKeySet()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Collection<V> values()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Set<Map.Entry<K, V>> entrySet()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread NavigableMap<K, V> descendingMap()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread NavigableMap<K, V> subMap( K a1, boolean a2, K a3, boolean a4)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread NavigableMap<K, V> headMap( K a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread NavigableMap<K, V> tailMap( K a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread SortedMap<K, V> subMap( K a1, K a2)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread SortedMap<K, V> headMap( K a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread SortedMap<K, V> tailMap( K a1)  { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
