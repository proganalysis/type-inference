package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface TypeCastTree extends ExpressionTree {
    @Polyread Tree getType(@Polyread TypeCastTree this) ;
    @Polyread ExpressionTree getExpression(@Polyread TypeCastTree this) ;
}
