package java.util;
import checkers.inference.reim.quals.*;

public abstract class AbstractSequentialList<E> extends AbstractList<E> {
  protected AbstractSequentialList() { throw new RuntimeException(("skeleton method")); }
  public E get(@Polyread AbstractSequentialList<E> this, int a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E set(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public void add(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public E remove(int a1) { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(int a1, @Readonly Collection<? extends E> a2) { throw new RuntimeException(("skeleton method")); }
  public @Polyread Iterator<E> iterator(@Polyread AbstractSequentialList<E> this)  { throw new RuntimeException(("skeleton method")); }
  public abstract @Polyread ListIterator<E> listIterator(@Polyread AbstractSequentialList<E> this, int a1) ;
}
