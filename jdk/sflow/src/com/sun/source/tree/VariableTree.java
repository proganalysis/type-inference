package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference.reim.quals.*;

public interface VariableTree extends StatementTree {
    @Polyread ModifiersTree getModifiers(@Polyread VariableTree this) ;
    @Polyread Name getName(@Polyread VariableTree this) ;
    @Polyread Tree getType(@Polyread VariableTree this) ;
    @Polyread ExpressionTree getInitializer(@Polyread VariableTree this) ;
}
