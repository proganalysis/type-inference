package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;

import checkers.inference2.reimN.quals.*;

public interface MethodTree extends Tree {
    @PolyPoly ModifiersTree getModifiers(@PolyPoly MethodTree this) ;
    @PolyPoly Name getName(@PolyPoly MethodTree this) ;
    @PolyPoly Tree getReturnType(@PolyPoly MethodTree this) ;
    @PolyPoly List<? extends TypeParameterTree> getTypeParameters(@PolyPoly MethodTree this) ;
    @PolyPoly List<? extends VariableTree> getParameters(@PolyPoly MethodTree this) ;
    @PolyPoly List<? extends AnnotationTree> getReceiverAnnotations(@PolyPoly MethodTree this) ;
    @PolyPoly List<? extends ExpressionTree> getThrows(@PolyPoly MethodTree this) ;
    @PolyPoly BlockTree getBody(@PolyPoly MethodTree this) ;
    @PolyPoly Tree getDefaultValue(@PolyPoly MethodTree this) ;
}
