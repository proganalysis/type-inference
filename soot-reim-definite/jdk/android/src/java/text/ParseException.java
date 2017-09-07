package java.text;
import checkers.inference.reim.quals.*;

public class ParseException extends Exception {
    // Added to avoid a warning
    private static final long serialVersionUID = 0;

    public ParseException(String s, int errorOffset) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int getErrorOffset ()  {
        throw new RuntimeException("skeleton method");
    }
}
