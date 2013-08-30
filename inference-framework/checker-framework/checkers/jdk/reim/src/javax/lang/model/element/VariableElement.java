package javax.lang.model.element;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import checkers.inference.reim.quals.*;

public interface VariableElement extends Element {
    @Polyread Object getConstantValue(@Polyread VariableElement this) ;
}
