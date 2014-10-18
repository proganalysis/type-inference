package java.util;
import checkers.inference.reim.quals.*;
import java.io.*;

public class LinkedHashMap<K,V>
    extends HashMap<K,V>
    implements Map<K,V>
{
    private static final long serialVersionUID = 0L;
    public LinkedHashMap(int initialCapacity, float loadFactor) { throw new RuntimeException("skeleton method"); }
    public LinkedHashMap(int initialCapacity) { throw new RuntimeException("skeleton method"); }
    public LinkedHashMap() { throw new RuntimeException("skeleton method"); }
    public LinkedHashMap(@Readonly Map<? extends K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) { throw new RuntimeException("skeleton method"); }
    public boolean containsValue(@Readonly LinkedHashMap<K,V> this, @Readonly Object value)  { throw new RuntimeException("skeleton method"); }
    public V get(@Polyread LinkedHashMap<K,V> this, @Readonly Object key)  { throw new RuntimeException("skeleton method"); } //WEI
    public void clear() { throw new RuntimeException("skeleton method"); }
    protected boolean removeEldestEntry(@Readonly Map.Entry<K,V> eldest) { throw new RuntimeException("skeleton method"); }
}
