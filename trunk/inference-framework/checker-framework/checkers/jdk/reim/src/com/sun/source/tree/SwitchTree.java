package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface SwitchTree extends StatementTree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly SwitchTree this) ;
    @PolyPoly List<? extends CaseTree> getCases(@PolyPoly SwitchTree this) ;
}
