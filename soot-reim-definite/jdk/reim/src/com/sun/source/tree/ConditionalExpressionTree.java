package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ConditionalExpressionTree extends ExpressionTree {
    @PolyreadThis @Polyread ExpressionTree getCondition() ;
    @PolyreadThis @Polyread ExpressionTree getTrueExpression() ;
    @PolyreadThis @Polyread ExpressionTree getFalseExpression() ;
}
