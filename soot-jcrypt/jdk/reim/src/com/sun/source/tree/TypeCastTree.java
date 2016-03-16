package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface TypeCastTree extends ExpressionTree {
    @PolyreadThis @Polyread Tree getType() ;
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
}
