package com.sun.source.tree;
import checkers.inference.reim.quals.*;

public interface AssignmentTree extends ExpressionTree {
    @Polyread ExpressionTree getVariable(@Polyread AssignmentTree this) ;
    @Polyread ExpressionTree getExpression(@Polyread AssignmentTree this) ;
}
