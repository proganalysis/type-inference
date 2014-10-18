package java.lang.ref;
import checkers.inference2.reimN.quals.*;

public abstract class Reference<T> {
    public T get(@PolyPoly Reference<T> this) {throw new RuntimeException("skeleton method");}
}