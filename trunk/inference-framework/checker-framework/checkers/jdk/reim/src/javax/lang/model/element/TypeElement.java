package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.*;
import javax.lang.model.util.*;

import checkers.inference.reim.quals.*;

public interface TypeElement extends Element, Parameterizable, QualifiedNameable {
    NestingKind getNestingKind(@Readonly TypeElement this) ;
    @Polyread Name getQualifiedName(@Polyread TypeElement this) ;
    TypeMirror getSuperclass();
    @Polyread List<? extends TypeMirror> getInterfaces(@Polyread TypeElement this) ;
    @Polyread List<? extends TypeParameterElement> getTypeParameters(@Polyread TypeElement this) ;
    @Polyread List<? extends Element> getEnclosedElements(@Polyread TypeElement this) ;
}
