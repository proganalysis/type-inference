package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface LiteralTree extends ExpressionTree {
    @PolyreadThis @Polyread Object getValue() ;
}
