package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface DoWhileLoopTree extends StatementTree {
    @PolyPoly ExpressionTree getCondition(@PolyPoly DoWhileLoopTree this) ;
    @PolyPoly StatementTree getStatement(@PolyPoly DoWhileLoopTree this) ;
}
