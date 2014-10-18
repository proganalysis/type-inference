package java.util;
import checkers.inference2.reimN.quals.*;

public interface SortedSet<E> extends Set<E> {
  public abstract Comparator<? super E> comparator(@ReadRead SortedSet<E> this) ;
  public abstract @PolyPoly SortedSet<E> subSet(@PolyPoly SortedSet<E> this, E a1, E a2) ;
  public abstract @PolyPoly SortedSet<E> headSet(@PolyPoly SortedSet<E> this, E a1) ;
  public abstract @PolyPoly SortedSet<E> tailSet(@PolyPoly SortedSet<E> this, E a1) ;
  public abstract E first(@PolyPoly SortedSet<E> this) ; //WEI
  public abstract E last(@PolyPoly SortedSet<E> this) ; //WEI
}
