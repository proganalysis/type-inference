package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ConditionalExpressionTree extends ExpressionTree {
    @Polyread ExpressionTree getCondition(@Polyread ConditionalExpressionTree this) ;
    @Polyread ExpressionTree getTrueExpression(@Polyread ConditionalExpressionTree this) ;
    @Polyread ExpressionTree getFalseExpression(@Polyread ConditionalExpressionTree this) ;
}
