package java.util;
import checkers.inference2.reimN.quals.*;

public abstract class AbstractSequentialList<E> extends AbstractList<E> {
  protected AbstractSequentialList() { throw new RuntimeException(("skeleton method")); }
  public E get(@PolyPoly AbstractSequentialList<E> this, int a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E set(int a1, @ReadRead E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public void add(int a1, @ReadRead E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public E remove(int a1) { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(int a1, @ReadRead Collection<? extends E> a2) { throw new RuntimeException(("skeleton method")); }
  public @PolyPoly Iterator<E> iterator(@PolyPoly AbstractSequentialList<E> this)  { throw new RuntimeException(("skeleton method")); }
  public abstract @PolyPoly ListIterator<E> listIterator(@PolyPoly AbstractSequentialList<E> this, int a1) ;
}
