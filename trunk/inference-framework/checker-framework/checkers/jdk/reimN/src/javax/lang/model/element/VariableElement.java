package javax.lang.model.element;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import checkers.inference2.reimN.quals.*;

public interface VariableElement extends Element {
    @PolyPoly Object getConstantValue(@PolyPoly VariableElement this) ;
}
