package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface CaseTree extends Tree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly CaseTree this) ;
    @PolyPoly List<? extends StatementTree> getStatements(@PolyPoly CaseTree this) ;
}
