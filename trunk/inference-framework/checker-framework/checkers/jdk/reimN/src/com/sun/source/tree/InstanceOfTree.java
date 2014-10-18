package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface InstanceOfTree extends ExpressionTree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly InstanceOfTree this) ;
    @PolyPoly Tree getType(@PolyPoly InstanceOfTree this) ;
}
