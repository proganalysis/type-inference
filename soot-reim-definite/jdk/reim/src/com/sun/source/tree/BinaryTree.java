package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface BinaryTree extends ExpressionTree {
    @PolyreadThis @Polyread ExpressionTree getLeftOperand() ;
    @PolyreadThis @Polyread ExpressionTree getRightOperand() ;
}
