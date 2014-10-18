package com.sun.source.tree;
import checkers.inference2.reimN.quals.*;

public interface AssertTree extends StatementTree {
    @PolyPoly ExpressionTree getCondition(@PolyPoly AssertTree this) ;
    @PolyPoly ExpressionTree getDetail(@PolyPoly AssertTree this) ;
}
