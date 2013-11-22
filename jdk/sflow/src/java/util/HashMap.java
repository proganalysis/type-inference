package java.util;
import checkers.inference.reim.quals.*;
import java.io.*;

import com.sun.jndi.url.rmi.*;

public class HashMap<K,V>
    extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {

    private static final long serialVersionUID = 0L;

    public HashMap(int initialCapacity, float loadFactor) { throw new RuntimeException("skeleton method"); }
    public HashMap(int initialCapacity) { throw new RuntimeException("skeleton method"); }
    public HashMap() { throw new RuntimeException("skeleton method"); }
    public HashMap(@Polyread HashMap<K,V> this, @Polyread Map<? extends K, ? extends V> m)  { throw new RuntimeException("skeleton method"); }
    public int size() { throw new RuntimeException("skeleton method"); }
    public boolean isEmpty() { throw new RuntimeException("skeleton method"); }
    public V get(@Polyread HashMap<K,V> this, @Readonly Object key)  { throw new RuntimeException("skeleton method"); } //WEI
    public boolean containsKey(@Readonly HashMap<K,V> this, @Readonly Object key)  { throw new RuntimeException("skeleton method"); }
    public V put(@Readonly K key, @Readonly V value) { throw new RuntimeException("skeleton method"); }
    public void putAll(@Readonly Map<? extends K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public V remove(@Readonly Object key) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    public boolean containsValue(@Readonly HashMap<K,V> this, @Readonly Object value)  { throw new RuntimeException("skeleton method"); }
    public Object clone(@Readonly HashMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public @Polyread Set<K> keySet(@Polyread HashMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public @Polyread Collection<V> values(@Polyread HashMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public @Polyread Set<@Polyread Map.Entry<K,V>> entrySet(@Polyread HashMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
}
