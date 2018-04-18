package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface WhileLoopTree extends StatementTree {
    @PolyreadThis @Polyread ExpressionTree getCondition() ;
    @PolyreadThis @Polyread StatementTree getStatement() ;
}
