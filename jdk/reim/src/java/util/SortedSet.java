package java.util;
import checkers.inference.reim.quals.*;

public interface SortedSet<E> extends Set<E> {
  public abstract Comparator<? super E> comparator(@Readonly SortedSet<E> this) ;
  public abstract @Polyread SortedSet<E> subSet(@Polyread SortedSet<E> this, E a1, E a2) ;
  public abstract @Polyread SortedSet<E> headSet(@Polyread SortedSet<E> this, E a1) ;
  public abstract @Polyread SortedSet<E> tailSet(@Polyread SortedSet<E> this, E a1) ;
  public abstract E first(@Polyread SortedSet<E> this) ; //WEI
  public abstract E last(@Polyread SortedSet<E> this) ; //WEI
}
