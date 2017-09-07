package javax.lang.model.element;

import checkers.inference.reim.quals.*;

public interface Name extends CharSequence {
    @ReadonlyThis boolean equals( @Readonly Object obj) ;
    @ReadonlyThis int hashCode() ;
    @ReadonlyThis boolean contentEquals( @Readonly CharSequence cs) ;
}
