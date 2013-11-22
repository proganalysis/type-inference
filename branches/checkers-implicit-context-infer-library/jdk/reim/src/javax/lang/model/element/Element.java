package javax.lang.model.element;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.*;
import javax.lang.model.util.*;

import checkers.inference.reim.quals.*;

public interface Element {
    TypeMirror asType(@Readonly Element this) ;
    ElementKind getKind(@Readonly Element this) ;
    List<? extends AnnotationMirror> getAnnotationMirrors(@Readonly Element this) ;
    <A extends Annotation> @Polyread A getAnnotation(@Polyread Element this, Class<A> annotationType) ;
    @Polyread Set<Modifier> getModifiers(@Polyread Element this) ;
    @Polyread Name getSimpleName(@Polyread Element this) ;
    @Polyread Element getEnclosingElement(@Polyread Element this) ;
    @Polyread List<? extends Element> getEnclosedElements(@Polyread Element this) ;
    boolean equals(@Readonly Element this, @Readonly Object obj) ;
    int hashCode(@Readonly Element this) ;
    <R, P> R accept(@Readonly Element this, ElementVisitor<R, P> v, P p) ;
}
