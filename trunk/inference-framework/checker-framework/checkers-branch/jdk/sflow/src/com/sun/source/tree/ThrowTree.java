package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ThrowTree extends StatementTree {
    @Polyread ExpressionTree getExpression(@Polyread ThrowTree this) ;
}
