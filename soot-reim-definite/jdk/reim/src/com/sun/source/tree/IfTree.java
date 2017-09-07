package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface IfTree extends StatementTree {
    @PolyreadThis @Polyread ExpressionTree getCondition() ;
    @PolyreadThis @Polyread StatementTree getThenStatement() ;
    @PolyreadThis @Polyread StatementTree getElseStatement() ;
}
