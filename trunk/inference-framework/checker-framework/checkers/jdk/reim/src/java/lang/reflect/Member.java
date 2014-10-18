package java.lang.reflect;
import checkers.inference2.reimN.quals.*;

public interface Member {
    public static final int PUBLIC = 0;
    public static final int DECLARED = 1;

    public Class<?> getDeclaringClass();

    public String getName();

    public int getModifiers();

    public boolean isSynthetic();
}
