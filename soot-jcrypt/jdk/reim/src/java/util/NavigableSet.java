package java.util;
import checkers.inference.reim.quals.*;

public interface NavigableSet<E> extends SortedSet<E> {
  @ReadonlyThis public abstract E lower(E a1) ;
  @ReadonlyThis public abstract E floor(E a1) ;
  @ReadonlyThis public abstract E ceiling(E a1) ;
  @ReadonlyThis public abstract E higher(E a1) ;
  public abstract E pollFirst();
  public abstract E pollLast();
  @PolyreadThis public abstract @Polyread Iterator<E> iterator() ;
  @PolyreadThis public abstract @Polyread NavigableSet<E> descendingSet() ;
  @PolyreadThis public abstract @Polyread Iterator<E> descendingIterator() ;
  @PolyreadThis public abstract @Polyread NavigableSet<E> subSet(E a1, boolean a2, E a3, boolean a4) ;
  @PolyreadThis public abstract @Polyread NavigableSet<E> headSet(E a1, boolean a2) ;
  @PolyreadThis public abstract @Polyread NavigableSet<E> tailSet(E a1, boolean a2) ;
  @PolyreadThis public abstract @Polyread SortedSet<E> subSet(E a1, E a2) ;
  @PolyreadThis public abstract @Polyread SortedSet<E> headSet(E a1) ;
  @PolyreadThis public abstract @Polyread SortedSet<E> tailSet(E a1) ;
}
