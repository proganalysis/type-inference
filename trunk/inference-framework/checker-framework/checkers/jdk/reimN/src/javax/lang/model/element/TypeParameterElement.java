package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

import checkers.inference2.reimN.quals.*;

public interface TypeParameterElement extends Element {
    @PolyPoly Element getGenericElement(@PolyPoly TypeParameterElement this) ;
    @PolyPoly List<? extends TypeMirror> getBounds(@PolyPoly TypeParameterElement this) ;
}
