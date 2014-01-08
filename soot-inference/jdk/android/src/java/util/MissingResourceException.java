package java.util;
import checkers.inference.reim.quals.*;

public class MissingResourceException extends RuntimeException {
    private static final long serialVersionUID = -4876345176062000401L;

    public MissingResourceException(String s, String className, String key) {
        throw new RuntimeException("skeleton method");
    }

    MissingResourceException(String message, String className, String key, Throwable cause) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getClassName()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getKey()  {
        throw new RuntimeException("skeleton method");
    }
}
