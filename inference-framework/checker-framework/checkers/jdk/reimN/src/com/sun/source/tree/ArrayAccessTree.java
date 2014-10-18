package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface ArrayAccessTree extends ExpressionTree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly ArrayAccessTree this) ;
    @PolyPoly ExpressionTree getIndex(@PolyPoly ArrayAccessTree this) ;
}
