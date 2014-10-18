package java.util;
import checkers.inference2.reimN.quals.*;

public interface NavigableSet<E> extends SortedSet<E> {
  public abstract E lower(@ReadRead NavigableSet<E> this, E a1) ;
  public abstract E floor(@ReadRead NavigableSet<E> this, E a1) ;
  public abstract E ceiling(@ReadRead NavigableSet<E> this, E a1) ;
  public abstract E higher(@ReadRead NavigableSet<E> this, E a1) ;
  public abstract E pollFirst();
  public abstract E pollLast();
  public abstract @PolyPoly Iterator<E> iterator(@PolyPoly NavigableSet<E> this) ;
  public abstract @PolyPoly NavigableSet<E> descendingSet(@PolyPoly NavigableSet<E> this) ;
  public abstract @PolyPoly Iterator<E> descendingIterator(@PolyPoly NavigableSet<E> this) ;
  public abstract @PolyPoly NavigableSet<E> subSet(@PolyPoly NavigableSet<E> this, E a1, boolean a2, E a3, boolean a4) ;
  public abstract @PolyPoly NavigableSet<E> headSet(@PolyPoly NavigableSet<E> this, E a1, boolean a2) ;
  public abstract @PolyPoly NavigableSet<E> tailSet(@PolyPoly NavigableSet<E> this, E a1, boolean a2) ;
  public abstract @PolyPoly SortedSet<E> subSet(@PolyPoly NavigableSet<E> this, E a1, E a2) ;
  public abstract @PolyPoly SortedSet<E> headSet(@PolyPoly NavigableSet<E> this, E a1) ;
  public abstract @PolyPoly SortedSet<E> tailSet(@PolyPoly NavigableSet<E> this, E a1) ;
}
