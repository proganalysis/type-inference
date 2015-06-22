package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ArrayAccessTree extends ExpressionTree {
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
    @PolyreadThis @Polyread ExpressionTree getIndex() ;
}
