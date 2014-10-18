package java.util;
import checkers.inference2.reimN.quals.*;

public class IdentityHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 0L;
  public IdentityHashMap() { throw new RuntimeException(("skeleton method")); }
  public IdentityHashMap(int a1) { throw new RuntimeException(("skeleton method")); }
  public IdentityHashMap(@PolyPoly IdentityHashMap<K, V> this, @PolyPoly Map<? extends K, ? extends V> a1)  { throw new RuntimeException(("skeleton method")); }
  public int size(@ReadRead IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public boolean isEmpty(@ReadRead IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public V get(@PolyPoly IdentityHashMap<K, V> this, Object a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public boolean containsKey(@ReadRead IdentityHashMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public boolean containsValue(@ReadRead IdentityHashMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public V put(@ReadRead K a1, @ReadRead V a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public void putAll(@ReadRead Map<? extends K, ? extends V> a1) { throw new RuntimeException(("skeleton method")); }
  public V remove(@ReadRead Object a1) { throw new RuntimeException(("skeleton method")); }
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public boolean equals(@ReadRead IdentityHashMap<K, V> this, @ReadRead Object a1)  { throw new RuntimeException(("skeleton method")); }
  public int hashCode(@ReadRead IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Set<K> keySet(@PolyPoly IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Collection<V> values(@PolyPoly IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Set<@PolyPoly Map.Entry<K, V>> entrySet(@PolyPoly IdentityHashMap<K, V> this)  { throw new RuntimeException(("skeleton method")); }
  public Object clone() { throw new RuntimeException("skeleton method"); }
}
