package java.util;
import checkers.inference.reim.quals.*;

public interface NavigableMap<K, V> extends SortedMap<K, V> {
  @PolyreadThis public abstract @Polyread Map.Entry<K, V> lowerEntry( K a1) ;
  @ReadonlyThis public abstract K lowerKey( K a1) ;
  @PolyreadThis public abstract @Polyread Map.Entry<K, V> floorEntry( K a1) ;
  @ReadonlyThis public abstract K floorKey( K a1) ;
  @PolyreadThis public abstract @Polyread Map.Entry<K, V> ceilingEntry( K a1) ;
  @ReadonlyThis public abstract K ceilingKey( K a1) ;
  @PolyreadThis public abstract @Polyread Map.Entry<K, V> higherEntry( K a1) ;
  @ReadonlyThis public abstract K higherKey( K a1) ;
  @PolyreadThis public abstract @Polyread Map.Entry<K, V> firstEntry() ;
  @PolyreadThis public abstract @Polyread Map.Entry<K, V> lastEntry() ;
  public abstract Map.Entry<K, V> pollFirstEntry();
  public abstract Map.Entry<K, V> pollLastEntry();
  @PolyreadThis public abstract @Polyread NavigableMap<K, V> descendingMap() ;
  @PolyreadThis public abstract @Polyread NavigableSet<K> navigableKeySet() ;
  @PolyreadThis public abstract @Polyread NavigableSet<K> descendingKeySet() ;
  @PolyreadThis public abstract @Polyread NavigableMap<K, V> subMap( K a1, boolean a2, K a3, boolean a4) ;
  @PolyreadThis public abstract @Polyread NavigableMap<K, V> headMap( K a1, boolean a2) ;
  @PolyreadThis public abstract @Polyread NavigableMap<K, V> tailMap( K a1, boolean a2) ;
  @PolyreadThis public abstract @Polyread SortedMap<K, V> subMap( K a1, K a2) ;
  @PolyreadThis public abstract @Polyread SortedMap<K, V> headMap( K a1) ;
  @PolyreadThis public abstract @Polyread SortedMap<K, V> tailMap( K a1) ;
}
