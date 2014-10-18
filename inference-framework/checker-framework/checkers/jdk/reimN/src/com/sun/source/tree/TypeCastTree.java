package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface TypeCastTree extends ExpressionTree {
    @PolyPoly Tree getType(@PolyPoly TypeCastTree this) ;
    @PolyPoly ExpressionTree getExpression(@PolyPoly TypeCastTree this) ;
}
