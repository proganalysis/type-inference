package com.sun.source.tree;
import checkers.inference.reim.quals.*;

public interface AssertTree extends StatementTree {
    @Polyread ExpressionTree getCondition(@Polyread AssertTree this) ;
    @Polyread ExpressionTree getDetail(@Polyread AssertTree this) ;
}
