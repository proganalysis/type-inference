package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface SwitchTree extends StatementTree {
    @Polyread ExpressionTree getExpression(@Polyread SwitchTree this) ;
    @Polyread List<? extends CaseTree> getCases(@Polyread SwitchTree this) ;
}
