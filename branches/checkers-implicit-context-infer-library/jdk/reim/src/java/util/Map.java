package java.util;
import checkers.inference.reim.quals.*;

public interface Map<K,V> {
    int size(@Readonly Map<K,V> this) ;
    boolean isEmpty(@Readonly Map<K,V> this) ;
    boolean containsKey(@Readonly Map<K,V> this, @Readonly Object key) ;
    boolean containsValue(@Readonly Map<K,V> this, @Readonly Object value) ;
    V get(@Polyread Map<K,V> this, @Readonly Object key) ; // WEI
    V put(@Readonly K key, @Readonly V value); //WEI K
    V remove(@Readonly Object key);
    void putAll(@Readonly Map<? extends K, ? extends V> m);
    void clear();
    @Polyread Set<K> keySet(@Polyread Map<K,V> this) ;
    @Polyread Collection<V> values(@Polyread Map<K,V> this) ;
    @Polyread Set<@Polyread Map.Entry<K, V>> entrySet(@Polyread Map<K,V> this) ;
    interface Entry<K,V> {
        K getKey(@Polyread Entry<K,V> this) ;  //WEI
        V getValue(@Polyread Entry<K,V> this) ;  //WEI 
        V setValue(@Readonly V value); 
        boolean equals(@Readonly Entry<K,V> this, @Readonly Object o) ;
        int hashCode(@Readonly Entry<K,V> this) ;
    }

    boolean equals(@Readonly Map<K,V> this, @Readonly Object o) ;
    int hashCode(@Readonly Map<K,V> this) ;
}
