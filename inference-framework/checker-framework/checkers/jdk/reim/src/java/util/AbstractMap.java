package java.util;
import checkers.inference2.reimN.quals.*;
import java.util.Map.Entry;

import com.sun.jndi.url.rmi.*;

public abstract class AbstractMap<K,V> implements Map<K,V> {
    protected AbstractMap() { throw new RuntimeException("skeleton method"); }
    public int size(@ReadRead AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public boolean isEmpty(@ReadRead AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public boolean containsValue(@ReadRead AbstractMap<K,V> this, @ReadRead Object value)  { throw new RuntimeException("skeleton method"); }
    public boolean containsKey(@ReadRead AbstractMap<K,V> this, @ReadRead Object key)  { throw new RuntimeException("skeleton method"); }
    public V get(@PolyPoly AbstractMap<K,V> this, @ReadRead Object key)  { throw new RuntimeException("skeleton method"); }  //WEI
    public V put(@ReadRead K key, @ReadRead V value) { throw new RuntimeException("skeleton method"); } // WEI K
    public V remove(@ReadRead Object key) { throw new RuntimeException("skeleton method"); }
    public void putAll(@ReadRead Map<? extends K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public void clear() { throw new RuntimeException("skeleton method"); }

    transient volatile Set<K>        keySet = null;
    transient volatile Collection<V> values = null;

    public @PolyPoly Set<K> keySet(@PolyPoly AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method");}
    public @PolyPoly Collection<V> values(@PolyPoly AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public abstract @PolyPoly Set<@PolyPoly Entry<K,V>> entrySet(@PolyPoly AbstractMap<K,V> this) ;
    public boolean equals(@ReadRead AbstractMap<K,V> this, @ReadRead Object o)  { throw new RuntimeException("skeleton method"); }
    public int hashCode(@ReadRead AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    public String toString(@ReadRead AbstractMap<K,V> this)  { throw new RuntimeException("skeleton method"); }
    protected Object clone(@ReadRead AbstractMap<K,V> this)  throws CloneNotSupportedException  { throw new RuntimeException("skeleton method"); }

    public static class SimpleEntry<K,V>
    implements Entry<K,V>, java.io.Serializable {
        private static final long serialVersionUID = 0L;
        public SimpleEntry(K key, V value) { throw new RuntimeException("skeleton method"); }
        public SimpleEntry(@ReadRead Entry<? extends K, ? extends V> entry) { throw new RuntimeException("skeleton method"); }
        public K getKey(@PolyPoly SimpleEntry<K,V> this)  { throw new RuntimeException("skeleton method"); }  //WEI
        public V getValue(@PolyPoly SimpleEntry<K,V> this)  { throw new RuntimeException("skeleton method"); } //WEI
        public V setValue(@ReadRead V value) { throw new RuntimeException("skeleton method"); } //WEI K
        public boolean equals(@ReadRead SimpleEntry<K,V> this, @ReadRead Object o)  { throw new RuntimeException("skeleton method"); }
        public int hashCode(@ReadRead SimpleEntry<K,V> this)  { throw new RuntimeException("skeleton method"); }
        public String toString(@ReadRead SimpleEntry<K,V> this)  { throw new RuntimeException("skeleton method"); }
    }

    public static class SimpleImmutableEntry<K,V>
    implements Entry<K,V>, java.io.Serializable {
        private static final long serialVersionUID = 0L;
        public SimpleImmutableEntry(K key, V value) { throw new RuntimeException("skeleton method"); }
        public SimpleImmutableEntry(@ReadRead Entry<? extends K, ? extends V> entry) { throw new RuntimeException("skeleton method"); }
        public K getKey() { throw new RuntimeException("skeleton method"); }
        public V getValue() { throw new RuntimeException("skeleton method"); }
        public V setValue(@ReadRead V value) { throw new RuntimeException("skeleton method"); } //WEI K
        public boolean equals(@ReadRead Object o) { throw new RuntimeException("skeleton method"); }
        public int hashCode(@ReadRead SimpleImmutableEntry<K,V> this)  { throw new RuntimeException("skeleton method"); }
        public String toString() { throw new RuntimeException("skeleton method"); }

    }
}
