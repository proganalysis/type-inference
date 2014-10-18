package java.util;
import checkers.inference2.reimN.quals.*;

public abstract class Dictionary<K,V> {

    public Dictionary() {    }
    abstract public int size(@ReadRead Dictionary<K,V> this) ;
    abstract public boolean isEmpty(@ReadRead Dictionary<K,V> this) ;
    abstract public @ReadRead Enumeration<K> keys(@ReadRead Dictionary<K,V> this) ;
    abstract public @ReadRead Enumeration<V> elements(@ReadRead Dictionary<K,V> this) ;
    abstract public V get(@ReadRead Dictionary<K,V> this, @ReadRead Object key) ;
    abstract public V put(@ReadRead K key, @ReadRead V value); //WEI
    abstract public V remove(@ReadRead Object key);
}
