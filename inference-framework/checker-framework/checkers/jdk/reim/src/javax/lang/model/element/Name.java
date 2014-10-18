package javax.lang.model.element;

import checkers.inference2.reimN.quals.*;

public interface Name extends CharSequence {
    boolean equals(@ReadRead Name this, @ReadRead Object obj) ;
    int hashCode(@ReadRead Name this) ;
    boolean contentEquals(@ReadRead Name this, @ReadRead CharSequence cs) ;
}
