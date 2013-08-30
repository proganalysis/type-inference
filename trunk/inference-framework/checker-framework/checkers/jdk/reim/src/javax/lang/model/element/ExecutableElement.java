package javax.lang.model.element;

import java.util.List;
import javax.lang.model.util.Types;
import javax.lang.model.type.*;

import checkers.inference.reim.quals.*;

public interface ExecutableElement extends Element, Parameterizable {
    @Polyread List<? extends TypeParameterElement> getTypeParameters(@Polyread ExecutableElement this) ;
    TypeMirror getReturnType(@Readonly ExecutableElement this) ;
    @Polyread List<? extends VariableElement> getParameters(@Polyread ExecutableElement this) ;
    boolean isVarArgs(@Readonly ExecutableElement this) ;
    @Polyread List<? extends TypeMirror> getThrownTypes(@Polyread ExecutableElement this) ;
    @Polyread AnnotationValue getDefaultValue(@Polyread ExecutableElement this) ;
}
