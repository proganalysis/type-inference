package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface IfTree extends StatementTree {
    @Polyread ExpressionTree getCondition(@Polyread IfTree this) ;
    @Polyread StatementTree getThenStatement(@Polyread IfTree this) ;
    @Polyread StatementTree getElseStatement(@Polyread IfTree this) ;
}
