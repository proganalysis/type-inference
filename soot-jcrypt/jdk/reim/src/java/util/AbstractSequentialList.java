package java.util;
import checkers.inference.reim.quals.*;

public abstract class AbstractSequentialList<E> extends AbstractList<E> {
  protected AbstractSequentialList() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public E get(int a1)  { throw new RuntimeException(("skeleton method")); } //WEI
  public E set(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public void add(int a1, @Readonly E a2) { throw new RuntimeException(("skeleton method")); } //WEI
  public E remove(int a1) { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(int a1, @Readonly Collection<? extends E> a2) { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public @Polyread Iterator<E> iterator()  { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public abstract @Polyread ListIterator<E> listIterator(int a1) ;
}
