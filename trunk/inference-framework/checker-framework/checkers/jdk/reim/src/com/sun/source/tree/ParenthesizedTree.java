package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ParenthesizedTree extends ExpressionTree {
    @Polyread ExpressionTree getExpression(@Polyread ParenthesizedTree this) ;
}
