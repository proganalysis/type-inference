package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface WhileLoopTree extends StatementTree {
    @Polyread ExpressionTree getCondition(@Polyread WhileLoopTree this) ;
    @Polyread StatementTree getStatement(@Polyread WhileLoopTree this) ;
}
