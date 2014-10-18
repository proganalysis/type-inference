package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface ThrowTree extends StatementTree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly ThrowTree this) ;
}
