package java.util;
import checkers.inference.reim.quals.*;
import java.util.Map.Entry;

import com.sun.jndi.url.rmi.*;

public abstract class AbstractMap<K,V> implements Map<K,V> {
    protected AbstractMap() { throw new RuntimeException("skeleton method"); }
    public int size(@Readonly AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public boolean isEmpty(@Readonly AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public boolean containsValue(@Readonly AbstractMap<K,V> this, @Readonly Object value)  { throw new RuntimeException("skeleton method"); }
    public boolean containsKey(@Readonly AbstractMap<K,V> this, @Readonly Object key)  { throw new RuntimeException("skeleton method"); }
    public V get(@Polyread AbstractMap<K,V> this, @Readonly Object key)  { throw new RuntimeException("skeleton method"); }  //WEI
    public V put(@Readonly K key, @Readonly V value) { throw new RuntimeException("skeleton method"); } // WEI K
    public V remove(@Readonly Object key) { throw new RuntimeException("skeleton method"); }
    public void putAll(@Readonly Map<? extends K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }

    transient volatile Set<K>        keySet = null;
    transient volatile Collection<V> values = null;

    public @Polyread Set<K> keySet(@Polyread AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method");}
    public @Polyread Collection<V> values(@Polyread AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public abstract @Polyread Set<@Polyread Entry<K,V>> entrySet(@Polyread AbstractMap<K,V> this) ;
    public boolean equals(@Readonly AbstractMap<K,V> this, @Readonly Object o)  { throw new RuntimeException("skeleton method"); }
    public int hashCode(@Readonly AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public String toString(@Readonly AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    protected Object clone(@Readonly AbstractMap<K,V> this)  throws CloneNotSupportedException  { throw new RuntimeException("skeleton method"); }

    public static class SimpleEntry<K,V>
    implements Entry<K,V>, java.io.Serializable {
        private static final long serialVersionUID = 0L;
        public SimpleEntry(K key, V value) { throw new RuntimeException("skeleton method"); }
        public SimpleEntry(@Readonly Entry<? extends K, ? extends V> entry) { throw new RuntimeException("skeleton method"); }
        public K getKey(@Polyread SimpleEntry<K,V> this)  { throw new RuntimeException("skeleton method"); }  //WEI
        public V getValue(@Polyread SimpleEntry<K,V> this)  { throw new RuntimeException("skeleton method"); } //WEI
        public V setValue(@Readonly V value) { throw new RuntimeException("skeleton method"); } //WEI K
        public boolean equals(@Readonly SimpleEntry<K,V> this, @Readonly Object o)  { throw new RuntimeException("skeleton method"); }
        public int hashCode(@Readonly SimpleEntry<K,V> this)  { throw new RuntimeException("skeleton method"); }
        public String toString(@Readonly SimpleEntry<K,V> this)  { throw new RuntimeException("skeleton method"); }
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
        public int hashCode(@Readonly SimpleImmutableEntry<K,V> this)  { throw new RuntimeException("skeleton method"); }
        public String toString() { throw new RuntimeException("skeleton method"); }

    }
}
