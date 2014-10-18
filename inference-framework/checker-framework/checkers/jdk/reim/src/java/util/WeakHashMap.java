package java.util;
import checkers.inference2.reimN.quals.*;

public class WeakHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
  public WeakHashMap(int a1, float a2) { throw new RuntimeException(("skeleton method")); }
  public WeakHashMap(int a1) { throw new RuntimeException(("skeleton method")); }
  public WeakHashMap() { throw new RuntimeException(("skeleton method")); }
  public WeakHashMap(@PolyPoly WeakHashMap<K, V> this, @PolyPoly Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public int size(@ReadRead WeakHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean isEmpty(@ReadRead WeakHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public V get(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean containsKey(@ReadRead WeakHashMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public V put(@ReadRead K a1, @ReadRead V a2) { throw new RuntimeException(("skeleton method")); } // WEI
  public void putAll(@ReadRead Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public V remove(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public boolean containsValue(@ReadRead WeakHashMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Set<K> keySet(@PolyPoly WeakHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Collection<V> values(@PolyPoly WeakHashMap<K, V> this) { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Set<Map.Entry<K, V>> entrySet(@PolyPoly WeakHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
}
