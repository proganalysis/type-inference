package java.util;
import checkers.inference.reim.quals.*;

public abstract class AbstractQueue<E> extends AbstractCollection<E> implements Queue<E> {
  protected AbstractQueue() { throw new RuntimeException(("skeleton method")); }
  public boolean add(@Readonly E a1) { throw new RuntimeException(("skeleton method")); } //WEI K
  public E remove() { throw new RuntimeException(("skeleton method")); }
  @PolyreadThis public E element()  { throw new RuntimeException(("skeleton method")); } //WEI
  public void clear() { throw new RuntimeException(("skeleton method")); }
  public boolean addAll(@Readonly Collection<? extends E> a1) { throw new RuntimeException(("skeleton method")); }
}
