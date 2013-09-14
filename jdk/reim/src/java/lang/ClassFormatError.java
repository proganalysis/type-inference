package java.lang;
import checkers.inference.reim.quals.*;

public class ClassFormatError extends LinkageError {
    private static final long serialVersionUID = 0L;
    public ClassFormatError() {
        throw new RuntimeException("skeleton method");
    }

    public ClassFormatError(String s) {
        throw new RuntimeException("skeleton method");
    }
}
