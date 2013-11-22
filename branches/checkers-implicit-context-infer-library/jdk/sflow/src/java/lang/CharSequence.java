package java.lang;
import checkers.inference.reim.quals.*;

public interface CharSequence {

    int length(@Readonly CharSequence this) ;

    char charAt(@Readonly CharSequence this, int index) ;
    @Polyread CharSequence subSequence(@Polyread CharSequence this, int start, int end) ;
    public String toString(@Readonly CharSequence this) ;

}
