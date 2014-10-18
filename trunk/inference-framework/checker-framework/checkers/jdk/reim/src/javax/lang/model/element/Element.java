package javax.lang.model.element;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.*;
import javax.lang.model.util.*;

import checkers.inference2.reimN.quals.*;

public interface Element {
    TypeMirror asType(@ReadRead Element this) ;
    ElementKind getKind(@ReadRead Element this) ;
    List<? extends AnnotationMirror> getAnnotationMirrors(@ReadRead Element this) ;
    <A extends Annotation> @PolyPoly A getAnnotation(@PolyPoly Element this, Class<A> annotationType) ;
    @PolyPoly Set<Modifier> getModifiers(@PolyPoly Element this) ;
    @PolyPoly Name getSimpleName(@PolyPoly Element this) ;
    @PolyPoly Element getEnclosingElement(@PolyPoly Element this) ;
    @PolyPoly List<? extends Element> getEnclosedElements(@PolyPoly Element this) ;
    boolean equals(@ReadRead Element this, @ReadRead Object obj) ;
    int hashCode(@ReadRead Element this) ;
    <R, P> R accept(@ReadRead Element this, ElementVisitor<R, P> v, P p) ;
}
