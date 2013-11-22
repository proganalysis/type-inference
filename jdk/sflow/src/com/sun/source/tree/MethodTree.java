package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;

import checkers.inference.reim.quals.*;

public interface MethodTree extends Tree {
    @Polyread ModifiersTree getModifiers(@Polyread MethodTree this) ;
    @Polyread Name getName(@Polyread MethodTree this) ;
    @Polyread Tree getReturnType(@Polyread MethodTree this) ;
    @Polyread List<? extends TypeParameterTree> getTypeParameters(@Polyread MethodTree this) ;
    @Polyread List<? extends VariableTree> getParameters(@Polyread MethodTree this) ;
    @Polyread List<? extends AnnotationTree> getReceiverAnnotations(@Polyread MethodTree this) ;
    @Polyread List<? extends ExpressionTree> getThrows(@Polyread MethodTree this) ;
    @Polyread BlockTree getBody(@Polyread MethodTree this) ;
    @Polyread Tree getDefaultValue(@Polyread MethodTree this) ;
}
