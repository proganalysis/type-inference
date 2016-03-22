package java.util;
import checkers.inference.reim.quals.*;

public abstract class Dictionary<K,V> {

    public Dictionary() {    }
    @ReadonlyThis abstract public int size() ;
    @ReadonlyThis abstract public boolean isEmpty() ;
    @ReadonlyThis abstract public @Readonly Enumeration<K> keys() ;
    @ReadonlyThis abstract public @Readonly Enumeration<V> elements() ;
    @ReadonlyThis abstract public V get(@Readonly Object key) ;
    abstract public V put(@Readonly K key, @Readonly V value); //WEI
    abstract public V remove(@Readonly Object key);
}
