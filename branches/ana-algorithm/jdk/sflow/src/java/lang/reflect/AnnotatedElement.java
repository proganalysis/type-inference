package java.lang.reflect;
import checkers.inference.reim.quals.*;

import java.lang.annotation.Annotation;

public @Readonly interface AnnotatedElement {

    boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);
    Annotation[] getAnnotations();
    Annotation[] getDeclaredAnnotations();
}
