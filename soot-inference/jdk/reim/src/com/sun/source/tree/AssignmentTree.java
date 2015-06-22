package com.sun.source.tree;
import checkers.inference.reim.quals.*;

public interface AssignmentTree extends ExpressionTree {
    @PolyreadThis @Polyread ExpressionTree getVariable() ;
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
}
