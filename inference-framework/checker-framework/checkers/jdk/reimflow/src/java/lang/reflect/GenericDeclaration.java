package java.lang.reflect;
import checkers.inference.reim.quals.*;

public @Readonly interface GenericDeclaration {
    public TypeVariable<?>[] getTypeParameters();
}
