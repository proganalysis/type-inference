package java.util;
import checkers.inference.reim.quals.*;

public class WeakHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
  public WeakHashMap(int a1, float a2) { throw new RuntimeException(("skeleton method")); }
  public WeakHashMap(int a1) { throw new RuntimeException(("skeleton method")); }
  public WeakHashMap() { throw new RuntimeException(("skeleton method")); }
  public WeakHashMap(@Polyread WeakHashMap<K, V> this, @Polyread Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public int size(@Readonly WeakHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean isEmpty(@Readonly WeakHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public V get(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public boolean containsKey(@Readonly WeakHashMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public V put(@Readonly K a1, @Readonly V a2) { throw new RuntimeException(("skeleton method")); } // WEI
  public void putAll(@Readonly Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public V remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public boolean containsValue(@Readonly WeakHashMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Set<K> keySet(@Polyread WeakHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Collection<V> values(@Polyread WeakHashMap<K, V> this) { throw new RuntimeException(("skeleton method")); }
  public @Polyread Set<Map.Entry<K, V>> entrySet(@Polyread WeakHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
}
