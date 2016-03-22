package java.util;
import checkers.inference.reim.quals.*;

public class WeakHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
  public WeakHashMap(int a1, float a2) { throw new RuntimeException(("skeleton method")); }
  public WeakHashMap(int a1) { throw new RuntimeException(("skeleton method")); }
  public WeakHashMap() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public WeakHashMap(@Polyread Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int size()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean isEmpty()  { throw new RuntimeException(("skeleton method")); }
  public V get(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean containsKey(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public V put(@Readonly K a1, @Readonly V a2) { throw new RuntimeException(("skeleton method")); } // WEI
  public void putAll(@Readonly Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public V remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean containsValue(@Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Set<K> keySet()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Collection<V> values() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Set<Map.Entry<K, V>> entrySet()  { throw new RuntimeException(("skeleton method")); }
}
