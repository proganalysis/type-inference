package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface NewClassTree extends ExpressionTree {
    @PolyreadThis @Polyread ExpressionTree getEnclosingExpression() ;
    @PolyreadThis @Polyread List<? extends Tree> getTypeArguments() ;
    @PolyreadThis @Polyread ExpressionTree getIdentifier() ;
    @PolyreadThis @Polyread List<? extends ExpressionTree> getArguments() ;
    @PolyreadThis @Polyread ClassTree getClassBody() ;
}
