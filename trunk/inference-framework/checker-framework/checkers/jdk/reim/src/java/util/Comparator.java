package java.util;
import checkers.inference2.reimN.quals.*;

public interface Comparator<T> {
    int compare(@ReadRead Comparator<T> this, @ReadRead T o1, @ReadRead T o2) ; //WEI
    boolean equals(@ReadRead Comparator<T> this, @ReadRead Object obj)  ;   //WEI
}
