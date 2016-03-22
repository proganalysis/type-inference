package java.lang.ref;
import checkers.inference.reim.quals.*;

public abstract class Reference<T> {
    @PolyreadThis public T get() {throw new RuntimeException("skeleton method");}
}
