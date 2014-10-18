package java.lang;
import checkers.inference2.reimN.quals.*;

public interface CharSequence {

    int length(@ReadRead CharSequence this) ;

    char charAt(@ReadRead CharSequence this, int index) ;
    @PolyPoly CharSequence subSequence(@PolyPoly CharSequence this, int start, int end) ;
    public String toString(@ReadRead CharSequence this) ;

}
