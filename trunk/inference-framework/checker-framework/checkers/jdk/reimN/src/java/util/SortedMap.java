package java.util;
import checkers.inference2.reimN.quals.*;

public interface SortedMap<K, V> extends Map<K, V> {
  public abstract Comparator<? super K> comparator(@ReadRead SortedMap<K, V> this) ;
  public abstract @PolyPoly SortedMap<K, V> subMap(@PolyPoly SortedMap<K, V> this, @ReadRead K a1, @ReadRead K a2) ;
  public abstract @PolyPoly SortedMap<K, V> headMap(@PolyPoly SortedMap<K, V> this, K a1) ;
  public abstract @PolyPoly SortedMap<K, V> tailMap(@PolyPoly SortedMap<K, V> this, K a1) ;
  public abstract K firstKey(@PolyPoly SortedMap<K, V> this) ; //WEI
  public abstract K lastKey(@PolyPoly SortedMap<K, V> this) ; //WEI
  public abstract @PolyPoly Set<K> keySet(@PolyPoly SortedMap<K, V> this) ;
  public abstract Collection<V> values(@PolyPoly SortedMap<K, V> this) ;
  public abstract @PolyPoly Set<@PolyPoly Map.Entry<K, V>> entrySet(@PolyPoly SortedMap<K, V> this) ;
}
