package java.lang;
import checkers.inference.reim.quals.*;

public interface CharSequence {

    @ReadonlyThis int length() ;

    @ReadonlyThis char charAt(int index) ;
    @PolyreadThis @Polyread CharSequence subSequence(int start, int end) ;
    @ReadonlyThis public String toString() ;

}
