package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface InstanceOfTree extends ExpressionTree {
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
    @PolyreadThis @Polyread Tree getType() ;
}
