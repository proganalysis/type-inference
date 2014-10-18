package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface IfTree extends StatementTree {
    @PolyPoly ExpressionTree getCondition(@PolyPoly IfTree this) ;
    @PolyPoly StatementTree getThenStatement(@PolyPoly IfTree this) ;
    @PolyPoly StatementTree getElseStatement(@PolyPoly IfTree this) ;
}
