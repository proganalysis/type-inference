package com.sun.source.tree;
import checkers.inference2.reimN.quals.*;

public interface ExpressionStatementTree extends StatementTree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly ExpressionStatementTree this) ;
}
