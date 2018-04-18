package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ReturnTree extends StatementTree {
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
}
