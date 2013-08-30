package java.util;
import checkers.inference.reim.quals.*;

public abstract class Dictionary<K,V> {

    public Dictionary() {    }
    abstract public int size(@Readonly Dictionary<K,V> this) ;
    abstract public boolean isEmpty(@Readonly Dictionary<K,V> this) ;
    abstract public @Readonly Enumeration<K> keys(@Readonly Dictionary<K,V> this) ;
    abstract public @Readonly Enumeration<V> elements(@Readonly Dictionary<K,V> this) ;
    abstract public V get(@Readonly Dictionary<K,V> this, @Readonly Object key) ;
    abstract public V put(@Readonly K key, @Readonly V value); //WEI
    abstract public V remove(@Readonly Object key);
}
