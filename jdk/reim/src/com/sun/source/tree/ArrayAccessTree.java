package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ArrayAccessTree extends ExpressionTree {
    @Polyread ExpressionTree getExpression(@Polyread ArrayAccessTree this) ;
    @Polyread ExpressionTree getIndex(@Polyread ArrayAccessTree this) ;
}
