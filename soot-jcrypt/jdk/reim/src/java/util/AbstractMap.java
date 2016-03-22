package java.util;
import checkers.inference.reim.quals.*;
import java.util.Map.Entry;

import com.sun.jndi.url.rmi.*;

public abstract class AbstractMap<K,V> implements Map<K,V> {
    protected AbstractMap() { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public int size()  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public boolean isEmpty()  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public boolean containsValue(@Readonly Object value)  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public boolean containsKey(@Readonly Object key)  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public V get(@Readonly Object key)  { throw new RuntimeException("skeleton method"); }  //WEI
    public V put(@Readonly K key, @Readonly V value) { throw new RuntimeException("skeleton method"); } // WEI K
    public V remove(@Readonly Object key) { throw new RuntimeException("skeleton method"); }
    public void putAll(@Readonly Map<? extends K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }

    transient volatile Set<K>        keySet = null;
    transient volatile Collection<V> values = null;

    @PolyreadThis public @Polyread Set<K> keySet()  { throw new RuntimeException("skeleton method");}
    @PolyreadThis public @Polyread Collection<V> values()  { throw new RuntimeException("skeleton method"); }
    @PolyreadThis public abstract @Polyread Set<Entry<K,V>> entrySet() ;
    @ReadonlyThis public boolean equals(@Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public int hashCode()  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis public String toString()  { throw new RuntimeException("skeleton method"); }
    @ReadonlyThis protected Object clone()  throws CloneNotSupportedException  { throw new RuntimeException("skeleton method"); }

    public static class SimpleEntry<K,V>
    implements Entry<K,V>, java.io.Serializable {
        private static final long serialVersionUID = 0L;
        public SimpleEntry(K key, V value) { throw new RuntimeException("skeleton method"); }
        public SimpleEntry(@Readonly Entry<? extends K, ? extends V> entry) { throw new RuntimeException("skeleton method"); }
        @PolyreadThis public K getKey()  { throw new RuntimeException("skeleton method"); }  //WEI
        @PolyreadThis public V getValue()  { throw new RuntimeException("skeleton method"); } //WEI
        public V setValue(@Readonly V value) { throw new RuntimeException("skeleton method"); } //WEI K
        @ReadonlyThis public boolean equals(@Readonly Object o)  { throw new RuntimeException("skeleton method"); }
        @ReadonlyThis public int hashCode()  { throw new RuntimeException("skeleton method"); }
        @ReadonlyThis public String toString()  { throw new RuntimeException("skeleton method"); }
    }

    public static class SimpleImmutableEntry<K,V>
    implements Entry<K,V>, java.io.Serializable {
        private static final long serialVersionUID = 0L;
        public SimpleImmutableEntry(K key, V value) { throw new RuntimeException("skeleton method"); }
        public SimpleImmutableEntry(@Readonly Entry<? extends K, ? extends V> entry) { throw new RuntimeException("skeleton method"); }
        public K getKey() { throw new RuntimeException("skeleton method"); }
        public V getValue() { throw new RuntimeException("skeleton method"); }
        public V setValue(@Readonly V value) { throw new RuntimeException("skeleton method"); } //WEI K
        public boolean equals(@Readonly Object o) { throw new RuntimeException("skeleton method"); }
        @ReadonlyThis public int hashCode()  { throw new RuntimeException("skeleton method"); }
        public String toString() { throw new RuntimeException("skeleton method"); }

    }
}
