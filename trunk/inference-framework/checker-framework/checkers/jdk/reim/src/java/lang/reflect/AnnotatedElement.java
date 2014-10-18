package java.lang.reflect;
import checkers.inference2.reimN.quals.*;

import java.lang.annotation.Annotation;

public @ReadRead interface AnnotatedElement {

    boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);
    Annotation[] getAnnotations();
    Annotation[] getDeclaredAnnotations();
}
