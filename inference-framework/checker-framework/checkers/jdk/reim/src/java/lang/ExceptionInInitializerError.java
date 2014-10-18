package java.lang;
import checkers.inference.reim.quals.*;

public class ExceptionInInitializerError extends LinkageError {
    private static final long serialVersionUID = 1521711792217232256L;

    public ExceptionInInitializerError() {
        throw new RuntimeException("skeleton method");
    }

    public ExceptionInInitializerError(Throwable thrown) {
        throw new RuntimeException("skeleton method");
    }

    public ExceptionInInitializerError(String s) {
        throw new RuntimeException("skeleton method");
    }

    public Throwable getException(@Readonly ExceptionInInitializerError this)  {
        throw new RuntimeException("skeleton method");
    }

    public Throwable getCause(@Readonly ExceptionInInitializerError this)  {
        throw new RuntimeException("skeleton method");
    }
}
