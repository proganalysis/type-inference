package com.sun.source.tree;
import checkers.inference.reim.quals.*;

public interface ExpressionStatementTree extends StatementTree {
    @Polyread ExpressionTree getExpression(@Polyread ExpressionStatementTree this) ;
}
