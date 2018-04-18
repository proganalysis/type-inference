package java.util;
import checkers.inference.reim.quals.*;

public interface SortedMap<K, V> extends Map<K, V> {
  @ReadonlyThis public abstract Comparator<? super K> comparator() ;
  @PolyreadThis public abstract @Polyread SortedMap<K, V> subMap( K a1, K a2) ;
  @PolyreadThis public abstract @Polyread SortedMap<K, V> headMap( K a1) ;
  @PolyreadThis public abstract @Polyread SortedMap<K, V> tailMap( K a1) ;
  @PolyreadThis public abstract K firstKey() ; //WEI
  @PolyreadThis public abstract K lastKey() ; //WEI
  @PolyreadThis public abstract @Polyread Set<K> keySet() ;
  @PolyreadThis public abstract Collection<V> values() ;
  @PolyreadThis public abstract @Polyread Set<Map.Entry<K, V>> entrySet() ;
}
