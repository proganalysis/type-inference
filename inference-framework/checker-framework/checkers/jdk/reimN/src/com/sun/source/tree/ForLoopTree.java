package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface ForLoopTree extends StatementTree {
    @PolyPoly List<? extends StatementTree> getInitializer(@PolyPoly ForLoopTree this) ;
    @PolyPoly ExpressionTree getCondition(@PolyPoly ForLoopTree this) ;
    @PolyPoly List<? extends ExpressionStatementTree> getUpdate(@PolyPoly ForLoopTree this) ;
    @PolyPoly StatementTree getStatement(@PolyPoly ForLoopTree this) ;
}
