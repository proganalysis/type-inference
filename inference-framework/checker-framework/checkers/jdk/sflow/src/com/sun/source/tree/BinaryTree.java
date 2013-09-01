package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface BinaryTree extends ExpressionTree {
    @Polyread ExpressionTree getLeftOperand(@Polyread BinaryTree this) ;
    @Polyread ExpressionTree getRightOperand(@Polyread BinaryTree this) ;
}
