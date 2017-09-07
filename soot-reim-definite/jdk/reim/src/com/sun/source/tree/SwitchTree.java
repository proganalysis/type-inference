package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface SwitchTree extends StatementTree {
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
    @PolyreadThis @Polyread List<? extends CaseTree> getCases() ;
}
