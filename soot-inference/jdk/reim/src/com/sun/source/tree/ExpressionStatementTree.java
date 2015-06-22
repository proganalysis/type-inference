package com.sun.source.tree;
import checkers.inference.reim.quals.*;

public interface ExpressionStatementTree extends StatementTree {
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
}
