package java.util;
import checkers.inference.reim.quals.*;

public interface NavigableSet<E> extends SortedSet<E> {
  public abstract E lower(@Readonly NavigableSet<E> this, E a1) ;
  public abstract E floor(@Readonly NavigableSet<E> this, E a1) ;
  public abstract E ceiling(@Readonly NavigableSet<E> this, E a1) ;
  public abstract E higher(@Readonly NavigableSet<E> this, E a1) ;
  public abstract E pollFirst();
  public abstract E pollLast();
  public abstract @Polyread Iterator<E> iterator(@Polyread NavigableSet<E> this) ;
  public abstract @Polyread NavigableSet<E> descendingSet(@Polyread NavigableSet<E> this) ;
  public abstract @Polyread Iterator<E> descendingIterator(@Polyread NavigableSet<E> this) ;
  public abstract @Polyread NavigableSet<E> subSet(@Polyread NavigableSet<E> this, E a1, boolean a2, E a3, boolean a4) ;
  public abstract @Polyread NavigableSet<E> headSet(@Polyread NavigableSet<E> this, E a1, boolean a2) ;
  public abstract @Polyread NavigableSet<E> tailSet(@Polyread NavigableSet<E> this, E a1, boolean a2) ;
  public abstract @Polyread SortedSet<E> subSet(@Polyread NavigableSet<E> this, E a1, E a2) ;
  public abstract @Polyread SortedSet<E> headSet(@Polyread NavigableSet<E> this, E a1) ;
  public abstract @Polyread SortedSet<E> tailSet(@Polyread NavigableSet<E> this, E a1) ;
}
