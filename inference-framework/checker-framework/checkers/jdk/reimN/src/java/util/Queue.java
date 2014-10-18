package java.util;
import checkers.inference2.reimN.quals.*;

public interface Queue<E> extends Collection<E> {
  public abstract boolean add(E a1);
  public abstract boolean offer(E a1);
  public abstract E remove();
  public abstract E poll();
  public abstract E element(@PolyPoly Queue<E> this) ; //WEI
  public abstract E peek(@PolyPoly Queue<E> this) ; //WEI
}
