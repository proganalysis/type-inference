package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.*;
import javax.lang.model.util.*;

import checkers.inference2.reimN.quals.*;

public interface TypeElement extends Element, Parameterizable, QualifiedNameable {
    NestingKind getNestingKind(@ReadRead TypeElement this) ;
    @PolyPoly Name getQualifiedName(@PolyPoly TypeElement this) ;
    TypeMirror getSuperclass();
    @PolyPoly List<? extends TypeMirror> getInterfaces(@PolyPoly TypeElement this) ;
    @PolyPoly List<? extends TypeParameterElement> getTypeParameters(@PolyPoly TypeElement this) ;
    @PolyPoly List<? extends Element> getEnclosedElements(@PolyPoly TypeElement this) ;
}
