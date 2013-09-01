package java.util;
import checkers.inference.reim.quals.*;

public class IdentityHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 0L;
  public IdentityHashMap() { throw new RuntimeException(("skeleton method")); }
  public IdentityHashMap(int a1) { throw new RuntimeException(("skeleton method")); }
  public IdentityHashMap(@Polyread IdentityHashMap<K, V> this, @Polyread Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public int size(@Readonly IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean isEmpty(@Readonly IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public V get(@Polyread IdentityHashMap<K, V> this, Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean containsKey(@Readonly IdentityHashMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsValue(@Readonly IdentityHashMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public V put(@Readonly K a1, @Readonly V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public void putAll(@Readonly Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public V remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public boolean equals(@Readonly IdentityHashMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public int hashCode(@Readonly IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Set<K> keySet(@Polyread IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Collection<V> values(@Polyread IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Set<@Polyread Map.Entry<K, V>> entrySet(@Polyread IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
