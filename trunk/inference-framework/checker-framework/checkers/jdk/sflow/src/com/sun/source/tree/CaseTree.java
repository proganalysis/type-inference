package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface CaseTree extends Tree {
    @Polyread ExpressionTree getExpression(@Polyread CaseTree this) ;
    @Polyread List<? extends StatementTree> getStatements(@Polyread CaseTree this) ;
}
