package java.util;
import checkers.inference.reim.quals.*;

public interface Comparator<T> {
    @ReadonlyThis int compare( @Readonly T o1, @Readonly T o2) ; //WEI
    @ReadonlyThis boolean equals( @Readonly Object obj)  ;   //WEI
}
