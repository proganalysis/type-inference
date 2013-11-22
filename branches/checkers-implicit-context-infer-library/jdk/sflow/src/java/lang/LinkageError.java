package java.lang;
import checkers.inference.reim.quals.*;

public
class LinkageError extends Error {
    private static final long serialVersionUID = 3579600108157160122L;

    public LinkageError() {
        throw new RuntimeException("skeleton method");
    }

    public LinkageError(String s) {
        throw new RuntimeException("skeleton method");
    }
}
