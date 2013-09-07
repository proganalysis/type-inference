package java.util;
import checkers.inference.reim.quals.*;

public interface Comparator<T> {
    int compare(@Readonly Comparator<T> this, @Readonly T o1, @Readonly T o2) ; //WEI
    boolean equals(@Readonly Comparator<T> this, @Readonly Object obj)  ;   //WEI
}
