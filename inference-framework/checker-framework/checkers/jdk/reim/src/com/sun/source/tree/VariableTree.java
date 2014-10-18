package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference2.reimN.quals.*;

public interface VariableTree extends StatementTree {
    @PolyPoly ModifiersTree getModifiers(@PolyPoly VariableTree this) ;
    @PolyPoly Name getName(@PolyPoly VariableTree this) ;
    @PolyPoly Tree getType(@PolyPoly VariableTree this) ;
    @PolyPoly ExpressionTree getInitializer(@PolyPoly VariableTree this) ;
}
