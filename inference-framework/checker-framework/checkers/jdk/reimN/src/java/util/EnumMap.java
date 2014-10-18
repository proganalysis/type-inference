package java.util;
import checkers.inference2.reimN.quals.*;

public class EnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 0L;
  public EnumMap(Class<K> a1) { throw new RuntimeException(("skeleton method")); }
  public EnumMap(@PolyPoly EnumMap<K, V> this, @PolyPoly EnumMap<K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public EnumMap(@PolyPoly EnumMap<K, V> this, @PolyPoly Map<K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public int size(@ReadRead EnumMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsValue(@ReadRead EnumMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsKey(@ReadRead EnumMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public V get(@PolyPoly EnumMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public V put(@ReadRead K a1, @ReadRead V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public V remove(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public void putAll(@ReadRead Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Set<K> keySet(@PolyPoly EnumMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Collection<V> values(@PolyPoly EnumMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Set<Map.Entry<K, V>> entrySet(@PolyPoly EnumMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean equals(@ReadRead EnumMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public EnumMap<K, V> clone() { throw new RuntimeException("skeleton method"); }
}
