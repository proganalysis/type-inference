package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.*;
import javax.lang.model.util.*;

import checkers.inference.reim.quals.*;

public interface TypeElement extends Element, Parameterizable, QualifiedNameable {
    @ReadonlyThis NestingKind getNestingKind() ;
    @PolyreadThis @Polyread Name getQualifiedName() ;
    TypeMirror getSuperclass();
    @PolyreadThis @Polyread List<? extends TypeMirror> getInterfaces() ;
    @PolyreadThis @Polyread List<? extends TypeParameterElement> getTypeParameters() ;
    @PolyreadThis @Polyread List<? extends Element> getEnclosedElements() ;
}
