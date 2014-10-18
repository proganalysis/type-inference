package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface ConditionalExpressionTree extends ExpressionTree {
    @PolyPoly ExpressionTree getCondition(@PolyPoly ConditionalExpressionTree this) ;
    @PolyPoly ExpressionTree getTrueExpression(@PolyPoly ConditionalExpressionTree this) ;
    @PolyPoly ExpressionTree getFalseExpression(@PolyPoly ConditionalExpressionTree this) ;
}
