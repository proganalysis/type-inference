package java.lang.reflect;
import checkers.inference.reim.quals.*;

// should extend ReflectiveOperationException in JDK7
public class InvocationTargetException extends Exception {
    private static final long serialVersionUID = 4085088731926701167L;

    protected InvocationTargetException() {
        throw new RuntimeException("skeleton method");
    }

    public InvocationTargetException(Throwable target) {
        throw new RuntimeException("skeleton method");
    }

    public InvocationTargetException(Throwable target, String s) {
        throw new RuntimeException("skeleton method");
    }

    public Throwable getTargetException(@Readonly InvocationTargetException this)  {
        throw new RuntimeException("skeleton method");
    }

    public Throwable getCause(@Readonly InvocationTargetException this)  {
        throw new RuntimeException("skeleton method");
    }
}
