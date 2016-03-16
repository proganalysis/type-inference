package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference.reim.quals.*;

public interface VariableTree extends StatementTree {
    @PolyreadThis @Polyread ModifiersTree getModifiers() ;
    @PolyreadThis @Polyread Name getName() ;
    @PolyreadThis @Polyread Tree getType() ;
    @PolyreadThis @Polyread ExpressionTree getInitializer() ;
}
