package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface BinaryTree extends ExpressionTree {
    @PolyPoly ExpressionTree getLeftOperand(@PolyPoly BinaryTree this) ;
    @PolyPoly ExpressionTree getRightOperand(@PolyPoly BinaryTree this) ;
}
