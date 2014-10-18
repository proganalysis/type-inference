package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ReturnTree extends StatementTree {
    @Polyread ExpressionTree getExpression(@Polyread ReturnTree this) ;
}
