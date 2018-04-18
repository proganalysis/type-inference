package java.util;
import checkers.inference.reim.quals.*;

public interface SortedSet<E> extends Set<E> {
  @ReadonlyThis public abstract Comparator<? super E> comparator() ;
  @PolyreadThis public abstract @Polyread SortedSet<E> subSet( E a1, E a2) ;
  @PolyreadThis public abstract @Polyread SortedSet<E> headSet( E a1) ;
  @PolyreadThis public abstract @Polyread SortedSet<E> tailSet( E a1) ;
  @PolyreadThis public abstract E first() ; //WEI
  @PolyreadThis public abstract E last() ; //WEI
}
