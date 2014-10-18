package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface InstanceOfTree extends ExpressionTree {
    @Polyread ExpressionTree getExpression(@Polyread InstanceOfTree this) ;
    @Polyread Tree getType(@Polyread InstanceOfTree this) ;
}
