package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface SynchronizedTree extends StatementTree {
    @Polyread ExpressionTree getExpression(@Polyread SynchronizedTree this) ;
    @Polyread BlockTree getBlock(@Polyread SynchronizedTree this) ;
}
