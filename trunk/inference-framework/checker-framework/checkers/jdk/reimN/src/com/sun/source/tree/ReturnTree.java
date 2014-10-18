package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface ReturnTree extends StatementTree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly ReturnTree this) ;
}
