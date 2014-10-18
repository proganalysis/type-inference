package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface UnaryTree extends ExpressionTree {
    @Polyread ExpressionTree getExpression(@Polyread UnaryTree this) ;
}
