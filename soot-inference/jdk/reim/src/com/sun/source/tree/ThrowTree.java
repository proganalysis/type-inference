package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ThrowTree extends StatementTree {
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
}
