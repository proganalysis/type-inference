package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface CaseTree extends Tree {
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
    @PolyreadThis @Polyread List<? extends StatementTree> getStatements() ;
}
