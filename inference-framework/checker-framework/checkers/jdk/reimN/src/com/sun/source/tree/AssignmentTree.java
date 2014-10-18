package com.sun.source.tree;
import checkers.inference2.reimN.quals.*;

public interface AssignmentTree extends ExpressionTree {
    @PolyPoly ExpressionTree getVariable(@PolyPoly AssignmentTree this) ;
    @PolyPoly ExpressionTree getExpression(@PolyPoly AssignmentTree this) ;
}
