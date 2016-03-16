package javax.lang.model.element;

import java.util.List;
import javax.lang.model.util.Types;
import javax.lang.model.type.*;

import checkers.inference.reim.quals.*;

public interface ExecutableElement extends Element, Parameterizable {
    @PolyreadThis @Polyread List<? extends TypeParameterElement> getTypeParameters() ;
    @ReadonlyThis TypeMirror getReturnType() ;
    @PolyreadThis @Polyread List<? extends VariableElement> getParameters() ;
    @ReadonlyThis boolean isVarArgs() ;
    @PolyreadThis @Polyread List<? extends TypeMirror> getThrownTypes() ;
    @PolyreadThis @Polyread AnnotationValue getDefaultValue() ;
}
