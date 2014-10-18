package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface UnaryTree extends ExpressionTree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly UnaryTree this) ;
}
