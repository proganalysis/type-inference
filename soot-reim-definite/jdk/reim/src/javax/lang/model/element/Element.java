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
    @ReadonlyThis TypeMirror asType() ;
    @ReadonlyThis ElementKind getKind() ;
    @ReadonlyThis List<? extends AnnotationMirror> getAnnotationMirrors() ;
    @PolyreadThis @Polyread <A extends Annotation> A getAnnotation( Class<A> annotationType) ;
    @PolyreadThis @Polyread Set<Modifier> getModifiers() ;
    @PolyreadThis @Polyread Name getSimpleName() ;
    @PolyreadThis @Polyread Element getEnclosingElement() ;
    @PolyreadThis @Polyread List<? extends Element> getEnclosedElements() ;
    @ReadonlyThis boolean equals( @Readonly Object obj) ;
    @ReadonlyThis int hashCode() ;
    @ReadonlyThis <R, P> R accept( ElementVisitor<R, P> v, P p) ;
}
