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
    @PolyreadThis public HashMap( @Polyread Map<? extends K, ? extends V> m)  { throw new RuntimeException("skeleton method"); }
    public int size() { throw new RuntimeException("skeleton method"); }
    public boolean isEmpty() { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public V get( @Readonly Object key)  { throw new RuntimeException("skeleton method"); } //WEI
    @ReadonlyThis public boolean containsKey( @Readonly Object key)  { throw new RuntimeException("skeleton method"); }
    public V put(@Readonly K key, @Readonly V value) { throw new RuntimeException("skeleton method"); }
    public void putAll(@Readonly Map<? extends K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public V remove(@Readonly Object key) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public boolean containsValue( @Readonly Object value)  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public Object clone()  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread Set<K> keySet()  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread Collection<V> values()  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public @Polyread Set<Map.Entry<K,V>> entrySet()  { throw new RuntimeException("skeleton method"); }
}
