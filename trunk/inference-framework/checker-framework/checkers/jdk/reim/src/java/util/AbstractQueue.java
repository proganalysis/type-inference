package java.util;
import checkers.inference2.reimN.quals.*;

public abstract class AbstractQueue<E> extends AbstractCollection<E> implements Queue<E> {
  protected AbstractQueue() { throw new RuntimeException(("skeleton method")); }
  public boolean add(@ReadRead E a1) { throw new RuntimeException(("skeleton method")); } //WEI K
  public E remove() { throw new RuntimeException(("skeleton method")); }
  public E element(@PolyPoly AbstractQueue<E> this)  { throw new RuntimeException(("skeleton method")); } //WEI
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(@ReadRead Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
}
