package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface DoWhileLoopTree extends StatementTree {
    @PolyreadThis @Polyread ExpressionTree getCondition() ;
    @PolyreadThis @Polyread StatementTree getStatement() ;
}
