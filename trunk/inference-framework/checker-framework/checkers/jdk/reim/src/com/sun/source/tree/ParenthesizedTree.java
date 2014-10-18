package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface ParenthesizedTree extends ExpressionTree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly ParenthesizedTree this) ;
}
