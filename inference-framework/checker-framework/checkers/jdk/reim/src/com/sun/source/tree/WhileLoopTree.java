package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface WhileLoopTree extends StatementTree {
    @PolyPoly ExpressionTree getCondition(@PolyPoly WhileLoopTree this) ;
    @PolyPoly StatementTree getStatement(@PolyPoly WhileLoopTree this) ;
}
