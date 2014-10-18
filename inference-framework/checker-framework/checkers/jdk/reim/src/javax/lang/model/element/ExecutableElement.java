package javax.lang.model.element;

import java.util.List;
import javax.lang.model.util.Types;
import javax.lang.model.type.*;

import checkers.inference2.reimN.quals.*;

public interface ExecutableElement extends Element, Parameterizable {
    @PolyPoly List<? extends TypeParameterElement> getTypeParameters(@PolyPoly ExecutableElement this) ;
    TypeMirror getReturnType(@ReadRead ExecutableElement this) ;
    @PolyPoly List<? extends VariableElement> getParameters(@PolyPoly ExecutableElement this) ;
    boolean isVarArgs(@ReadRead ExecutableElement this) ;
    @PolyPoly List<? extends TypeMirror> getThrownTypes(@PolyPoly ExecutableElement this) ;
    @PolyPoly AnnotationValue getDefaultValue(@PolyPoly ExecutableElement this) ;
}
