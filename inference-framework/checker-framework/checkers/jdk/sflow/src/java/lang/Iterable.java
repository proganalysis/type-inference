package java.lang;
import checkers.inference.reim.quals.*;

import java.util.Iterator;

public interface Iterable<T> {
     @Polyread Iterator<T> iterator(@Polyread Iterable<T> this) ;
}
