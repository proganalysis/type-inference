package java.util;
import checkers.inference.reim.quals.*;

public interface SortedMap<K, V> extends Map<K, V> {
  public abstract Comparator<? super K> comparator(@Readonly SortedMap<K, V> this) ;
  public abstract @Polyread SortedMap<K, V> subMap(@Polyread SortedMap<K, V> this, K a1, K a2) ;
  public abstract @Polyread SortedMap<K, V> headMap(@Polyread SortedMap<K, V> this, K a1) ;
  public abstract @Polyread SortedMap<K, V> tailMap(@Polyread SortedMap<K, V> this, K a1) ;
  public abstract K firstKey(@Polyread SortedMap<K, V> this) ; //WEI
  public abstract K lastKey(@Polyread SortedMap<K, V> this) ; //WEI
  public abstract @Polyread Set<K> keySet(@Polyread SortedMap<K, V> this) ;
  public abstract @Polyread Collection<V> values(@Polyread SortedMap<K, V> this) ;
  public abstract @Polyread Set<@Polyread Map.Entry<K, V>> entrySet(@Polyread SortedMap<K, V> this) ;
}
