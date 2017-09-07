package java.util;
import checkers.inference.reim.quals.*;

public class IdentityHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 0L;
  public IdentityHashMap() { throw new RuntimeException(("skeleton method")); }
  public IdentityHashMap(int a1) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public IdentityHashMap( @Polyread Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int size()  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean isEmpty()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public V get( Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  @ReadonlyThis public boolean containsKey( @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean containsValue( @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  public V put(@Readonly K a1, @Readonly V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public void putAll(@Readonly Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public V remove(@Readonly Object a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public boolean equals( @Readonly Object a1)  { throw new RuntimeException(("skeleton method")); }
  @ReadonlyThis public int hashCode()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Set<K> keySet()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Collection<V> values()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Set<Map.Entry<K, V>> entrySet()  { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
