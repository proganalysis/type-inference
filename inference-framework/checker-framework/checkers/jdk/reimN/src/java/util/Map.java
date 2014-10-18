package java.util;
import checkers.inference2.reimN.quals.*;

public interface Map<K,V> {
    int size(@ReadRead Map<K,V> this) ;
    boolean isEmpty(@ReadRead Map<K,V> this) ;
    boolean containsKey(@ReadRead Map<K,V> this, @ReadRead Object key) ;
    boolean containsValue(@ReadRead Map<K,V> this, @ReadRead Object value) ;
    V get(@PolyPoly Map<K,V> this, @ReadRead Object key) ; // WEI
    V put(@ReadRead K key, @ReadRead V value); //WEI K
    V remove(@ReadRead Object key);
    void putAll(@ReadRead Map<? extends K, ? extends V> m);
    void clear();
    Set<K> keySet(@PolyPoly Map<K,V> this) ;
    Collection<V> values(@PolyPoly Map<K,V> this) ;
    @PolyPoly Set<@PolyPoly Map.Entry<K, V>> entrySet(@PolyPoly Map<K,V> this) ;
    interface Entry<K,V> {
        K getKey(@PolyPoly Entry<K,V> this) ;  //WEI
        V getValue(@PolyPoly Entry<K,V> this) ;  //WEI 
        V setValue(@ReadRead V value); 
        boolean equals(@ReadRead Entry<K,V> this, @ReadRead Object o) ;
        int hashCode(@ReadRead Entry<K,V> this) ;
    }

    boolean equals(@ReadRead Map<K,V> this, @ReadRead Object o) ;
    int hashCode(@ReadRead Map<K,V> this) ;
}
