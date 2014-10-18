package java.util;
import checkers.inference2.reimN.quals.*;

public interface NavigableMap<K, V> extends SortedMap<K, V> {
  public abstract @PolyPoly Map.Entry<K, V> lowerEntry(@PolyPoly NavigableMap<K, V> this, K a1) ;
  public abstract K lowerKey(@ReadRead NavigableMap<K, V> this, K a1) ;
  public abstract @PolyPoly Map.Entry<K, V> floorEntry(@PolyPoly NavigableMap<K, V> this, K a1) ;
  public abstract K floorKey(@ReadRead NavigableMap<K, V> this, K a1) ;
  public abstract @PolyPoly Map.Entry<K, V> ceilingEntry(@PolyPoly NavigableMap<K, V> this, K a1) ;
  public abstract K ceilingKey(@ReadRead NavigableMap<K, V> this, K a1) ;
  public abstract @PolyPoly Map.Entry<K, V> higherEntry(@PolyPoly NavigableMap<K, V> this, K a1) ;
  public abstract K higherKey(@ReadRead NavigableMap<K, V> this, K a1) ;
  public abstract @PolyPoly Map.Entry<K, V> firstEntry(@PolyPoly NavigableMap<K, V> this) ;
  public abstract @PolyPoly Map.Entry<K, V> lastEntry(@PolyPoly NavigableMap<K, V> this) ;
  public abstract Map.Entry<K, V> pollFirstEntry();
  public abstract Map.Entry<K, V> pollLastEntry();
  public abstract @PolyPoly NavigableMap<K, V> descendingMap(@PolyPoly NavigableMap<K, V> this) ;
  public abstract @PolyPoly NavigableSet<K> navigableKeySet(@PolyPoly NavigableMap<K, V> this) ;
  public abstract @PolyPoly NavigableSet<K> descendingKeySet(@PolyPoly NavigableMap<K, V> this) ;
  public abstract @PolyPoly NavigableMap<K, V> subMap(@PolyPoly NavigableMap<K, V> this, K a1, boolean a2, K a3, boolean a4) ;
  public abstract @PolyPoly NavigableMap<K, V> headMap(@PolyPoly NavigableMap<K, V> this, K a1, boolean a2) ;
  public abstract @PolyPoly NavigableMap<K, V> tailMap(@PolyPoly NavigableMap<K, V> this, K a1, boolean a2) ;
  public abstract @PolyPoly SortedMap<K, V> subMap(@PolyPoly NavigableMap<K, V> this, K a1, K a2) ;
  public abstract @PolyPoly SortedMap<K, V> headMap(@PolyPoly NavigableMap<K, V> this, K a1) ;
  public abstract @PolyPoly SortedMap<K, V> tailMap(@PolyPoly NavigableMap<K, V> this, K a1) ;
}
