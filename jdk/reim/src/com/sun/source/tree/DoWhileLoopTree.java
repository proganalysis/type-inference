package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface DoWhileLoopTree extends StatementTree {
    @Polyread ExpressionTree getCondition(@Polyread DoWhileLoopTree this) ;
    @Polyread StatementTree getStatement(@Polyread DoWhileLoopTree this) ;
}
