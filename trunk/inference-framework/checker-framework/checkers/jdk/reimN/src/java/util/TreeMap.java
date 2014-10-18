package java.util;
import checkers.inference2.reimN.quals.*;

public class TreeMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 0L;
  public TreeMap() { throw new RuntimeException(("skeleton method")); }
  public TreeMap(Comparator<? super K> a1) { throw new RuntimeException(("skeleton method")); }
  public TreeMap(@PolyPoly TreeMap<K, V> this, @PolyPoly Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public TreeMap(@PolyPoly TreeMap<K, V> this, @PolyPoly SortedMap<K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public int size(@ReadRead TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsKey(@ReadRead TreeMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsValue(@ReadRead TreeMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public V get(@PolyPoly TreeMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public Comparator<? super K> comparator(@ReadRead TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public K firstKey(@PolyPoly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public K lastKey(@PolyPoly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public void putAll(@ReadRead Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public V put(@ReadRead K a1, @ReadRead V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public V remove(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); } // WEI @ReadRead was removed
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Map.Entry<K, V> firstEntry(@PolyPoly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Map.Entry<K, V> lastEntry(@PolyPoly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public Map.Entry<K, V> pollFirstEntry() { throw new RuntimeException(("skeleton method")); }
  public Map.Entry<K, V> pollLastEntry() { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Map.Entry<K, V> lowerEntry(@PolyPoly TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public K lowerKey(@PolyPoly TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public @PolyPoly Map.Entry<K, V> floorEntry(@PolyPoly TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public K floorKey(@PolyPoly TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public @PolyPoly Map.Entry<K, V> ceilingEntry(@PolyPoly TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public K ceilingKey(@PolyPoly TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public @PolyPoly Map.Entry<K, V> higherEntry(@PolyPoly TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public K higherKey(@PolyPoly TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public Set<K> keySet(@PolyPoly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly NavigableSet<K> navigableKeySet(@PolyPoly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly NavigableSet<K> descendingKeySet(@PolyPoly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Collection<V> values(@PolyPoly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Set<Map.Entry<K, V>> entrySet(@PolyPoly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly NavigableMap<K, V> descendingMap(@PolyPoly TreeMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly NavigableMap<K, V> subMap(@PolyPoly TreeMap<K, V> this, K a1, boolean a2, K a3, boolean a4)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly NavigableMap<K, V> headMap(@PolyPoly TreeMap<K, V> this, K a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly NavigableMap<K, V> tailMap(@PolyPoly TreeMap<K, V> this, K a1, boolean a2)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly SortedMap<K, V> subMap(@PolyPoly TreeMap<K, V> this, K a1, K a2)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly SortedMap<K, V> headMap(@PolyPoly TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly SortedMap<K, V> tailMap(@PolyPoly TreeMap<K, V> this, K a1)  { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
