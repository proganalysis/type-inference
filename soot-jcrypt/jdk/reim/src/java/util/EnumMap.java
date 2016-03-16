package java.util;
import checkers.inference.reim.quals.*;

public class EnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 0L;
  public EnumMap(Class<K> a1) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public EnumMap( @Polyread EnumMap<K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public EnumMap( @Polyread Map<K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int size()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean containsValue( @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean containsKey( @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public V get( @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public V put(@Readonly K a1, @Readonly V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public V remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public void putAll(@Readonly Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Set<K> keySet()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Collection<V> values()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Set<Map.Entry<K, V>> entrySet()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean equals( @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public EnumMap<K, V> clone() { throw new RuntimeException("skeleton method"); }
}
