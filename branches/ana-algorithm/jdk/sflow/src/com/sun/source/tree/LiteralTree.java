package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface LiteralTree extends ExpressionTree {
    @Polyread Object getValue(@Polyread LiteralTree this) ;
}
