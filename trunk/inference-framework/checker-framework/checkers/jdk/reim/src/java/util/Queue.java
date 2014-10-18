package java.util;
import checkers.inference.reim.quals.*;

public interface Queue<E> extends Collection<E> {
  public abstract boolean add(E a1);
  public abstract boolean offer(E a1);
  public abstract E remove();
  public abstract E poll();
  public abstract E element(@Polyread Queue<E> this) ; //WEI
  public abstract E peek(@Polyread Queue<E> this) ; //WEI
}
