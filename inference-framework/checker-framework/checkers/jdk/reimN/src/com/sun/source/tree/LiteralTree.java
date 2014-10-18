package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface LiteralTree extends ExpressionTree {
    @PolyPoly Object getValue(@PolyPoly LiteralTree this) ;
}
