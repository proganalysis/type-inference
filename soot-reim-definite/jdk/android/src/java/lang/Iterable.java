package java.lang;
import checkers.inference.reim.quals.*;

import java.util.Iterator;

public interface Iterable<T> {
     @PolyreadThis @Polyread Iterator<T> iterator() ;
}
