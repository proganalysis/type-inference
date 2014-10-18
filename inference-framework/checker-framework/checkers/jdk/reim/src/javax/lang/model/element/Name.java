package javax.lang.model.element;

import checkers.inference.reim.quals.*;

public interface Name extends CharSequence {
    boolean equals(@Readonly Name this, @Readonly Object obj) ;
    int hashCode(@Readonly Name this) ;
    boolean contentEquals(@Readonly Name this, @Readonly CharSequence cs) ;
}
