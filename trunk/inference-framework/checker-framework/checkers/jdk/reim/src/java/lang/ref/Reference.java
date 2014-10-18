package java.lang.ref;
import checkers.inference.reim.quals.*;

public abstract class Reference<T> {
    public T get(@Polyread Reference<T> this) {throw new RuntimeException("skeleton method");}
}