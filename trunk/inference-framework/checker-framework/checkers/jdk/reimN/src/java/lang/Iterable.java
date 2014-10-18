package java.lang;
import checkers.inference2.reimN.quals.*;

import java.util.Iterator;

public interface Iterable<T> {
     @PolyPoly Iterator<T> iterator(@PolyPoly Iterable<T> this) ;
}
