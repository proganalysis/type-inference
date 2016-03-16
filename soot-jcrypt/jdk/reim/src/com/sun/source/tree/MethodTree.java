package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;

import checkers.inference.reim.quals.*;

public interface MethodTree extends Tree {
    @PolyreadThis @Polyread ModifiersTree getModifiers() ;
    @PolyreadThis @Polyread Name getName() ;
    @PolyreadThis @Polyread Tree getReturnType() ;
    @PolyreadThis @Polyread List<? extends TypeParameterTree> getTypeParameters() ;
    @PolyreadThis @Polyread List<? extends VariableTree> getParameters() ;
    @PolyreadThis @Polyread List<? extends AnnotationTree> getReceiverAnnotations() ;
    @PolyreadThis @Polyread List<? extends ExpressionTree> getThrows() ;
    @PolyreadThis @Polyread BlockTree getBody() ;
    @PolyreadThis @Polyread Tree getDefaultValue() ;
}
