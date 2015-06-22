package java.util;
import checkers.inference.reim.quals.*;

public interface Map<K,V> {
    @ReadonlyThis int size() ;
    @ReadonlyThis boolean isEmpty() ;
    @ReadonlyThis boolean containsKey( @Readonly Object key) ;
    @ReadonlyThis boolean containsValue( @Readonly Object value) ;
    @PolyreadThis V get( @Readonly Object key) ; // WEI
    V put(@Readonly K key, @Readonly V value); //WEI K
    V remove(@Readonly Object key);
    void putAll(@Readonly Map<? extends K, ? extends V> m);
    void clear();
    //@PolyreadThis @Polyread Set<K> keySet() ;
    @PolyreadThis Set<K> keySet() ;  //Yao
    @PolyreadThis Collection<V> values() ;
    @PolyreadThis @Polyread Set<Map.Entry<K, V>> entrySet() ;
    interface Entry<K,V> {
        @PolyreadThis K getKey() ;  //WEI
        @PolyreadThis V getValue() ;  //WEI 
        V setValue(@Readonly V value); 
        @ReadonlyThis boolean equals( @Readonly Object o) ;
        @ReadonlyThis int hashCode() ;
    }

    @ReadonlyThis boolean equals( @Readonly Object o) ;
    @ReadonlyThis int hashCode() ;
}
