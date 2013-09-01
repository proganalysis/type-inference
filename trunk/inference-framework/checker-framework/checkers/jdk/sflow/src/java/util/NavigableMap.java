package java.util;
import checkers.inference.reim.quals.*;

public interface NavigableMap<K, V> extends SortedMap<K, V> {
  public abstract @Polyread Map.Entry<K, V> lowerEntry(@Polyread NavigableMap<K, V> this, K a1) ;
  public abstract K lowerKey(@Readonly NavigableMap<K, V> this, K a1) ;
  public abstract @Polyread Map.Entry<K, V> floorEntry(@Polyread NavigableMap<K, V> this, K a1) ;
  public abstract K floorKey(@Readonly NavigableMap<K, V> this, K a1) ;
  public abstract @Polyread Map.Entry<K, V> ceilingEntry(@Polyread NavigableMap<K, V> this, K a1) ;
  public abstract K ceilingKey(@Readonly NavigableMap<K, V> this, K a1) ;
  public abstract @Polyread Map.Entry<K, V> higherEntry(@Polyread NavigableMap<K, V> this, K a1) ;
  public abstract K higherKey(@Readonly NavigableMap<K, V> this, K a1) ;
  public abstract @Polyread Map.Entry<K, V> firstEntry(@Polyread NavigableMap<K, V> this) ;
  public abstract @Polyread Map.Entry<K, V> lastEntry(@Polyread NavigableMap<K, V> this) ;
  public abstract Map.Entry<K, V> pollFirstEntry();
  public abstract Map.Entry<K, V> pollLastEntry();
  public abstract @Polyread NavigableMap<K, V> descendingMap(@Polyread NavigableMap<K, V> this) ;
  public abstract @Polyread NavigableSet<K> navigableKeySet(@Polyread NavigableMap<K, V> this) ;
  public abstract @Polyread NavigableSet<K> descendingKeySet(@Polyread NavigableMap<K, V> this) ;
  public abstract @Polyread NavigableMap<K, V> subMap(@Polyread NavigableMap<K, V> this, K a1, boolean a2, K a3, boolean a4) ;
  public abstract @Polyread NavigableMap<K, V> headMap(@Polyread NavigableMap<K, V> this, K a1, boolean a2) ;
  public abstract @Polyread NavigableMap<K, V> tailMap(@Polyread NavigableMap<K, V> this, K a1, boolean a2) ;
  public abstract @Polyread SortedMap<K, V> subMap(@Polyread NavigableMap<K, V> this, K a1, K a2) ;
  public abstract @Polyread SortedMap<K, V> headMap(@Polyread NavigableMap<K, V> this, K a1) ;
  public abstract @Polyread SortedMap<K, V> tailMap(@Polyread NavigableMap<K, V> this, K a1) ;
}
