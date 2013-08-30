package java.util;
import checkers.inference.reim.quals.*;

public class EnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 0L;
  public EnumMap(Class<K> a1) { throw new RuntimeException(("skeleton method")); }
  public EnumMap(@Polyread EnumMap<K, V> this, @Polyread EnumMap<K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public EnumMap(@Polyread EnumMap<K, V> this, @Polyread Map<K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public int size(@Readonly EnumMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsValue(@Readonly EnumMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsKey(@Readonly EnumMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public V get(@Polyread EnumMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public V put(@Readonly K a1, @Readonly V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public V remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public void putAll(@Readonly Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public @Polyread Set<K> keySet(@Polyread EnumMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Collection<V> values(@Polyread EnumMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @Polyread Set<Map.Entry<K, V>> entrySet(@Polyread EnumMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean equals(@Readonly EnumMap<K, V> this, @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public EnumMap<K, V> clone() { throw new RuntimeException("skeleton method"); }
}
