package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface SynchronizedTree extends StatementTree {
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
    @PolyreadThis @Polyread BlockTree getBlock() ;
}
