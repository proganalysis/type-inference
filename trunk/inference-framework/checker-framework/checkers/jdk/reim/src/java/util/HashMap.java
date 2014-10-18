package java.util;
import checkers.inference2.reimN.quals.*;
import java.io.*;

import com.sun.jndi.url.rmi.*;

public class HashMap<K,V>
    extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {

    private static final long serialVersionUID = 0L;

    public HashMap(int initialCapacity, float loadFactor) { throw new RuntimeException("skeleton method"); }
    public HashMap(int initialCapacity) { throw new RuntimeException("skeleton method"); }
    public HashMap() { throw new RuntimeException("skeleton method"); }
    public HashMap(@PolyPoly HashMap<K,V> this, @PolyPoly Map<? extends K, ? extends V> m)  { throw new RuntimeException("skeleton method"); }
    public int size() { throw new RuntimeException("skeleton method"); }
    public boolean isEmpty() { throw new RuntimeException("skeleton method"); }
    public V get(@PolyPoly HashMap<K,V> this, @ReadRead Object key)  { throw new RuntimeException("skeleton method"); } //WEI
    public boolean containsKey(@ReadRead HashMap<K,V> this, @ReadRead Object key)  { throw new RuntimeException("skeleton method"); }
    public V put(@ReadRead K key, @ReadRead V value) { throw new RuntimeException("skeleton method"); }
    public void putAll(@ReadRead Map<? extends K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public V remove(@ReadRead Object key) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    public boolean containsValue(@ReadRead HashMap<K,V> this, @ReadRead Object value)  { throw new RuntimeException("skeleton method"); }
    public Object clone(@ReadRead HashMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public Set<K> keySet(@PolyPoly HashMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public Collection<V> values(@PolyPoly HashMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public @PolyPoly Set<@PolyPoly Map.Entry<K,V>> entrySet(@PolyPoly HashMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
}
