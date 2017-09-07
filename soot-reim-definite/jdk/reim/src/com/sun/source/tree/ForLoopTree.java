package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface ForLoopTree extends StatementTree {
    @PolyreadThis @Polyread List<? extends StatementTree> getInitializer() ;
    @PolyreadThis @Polyread ExpressionTree getCondition() ;
    @PolyreadThis @Polyread List<? extends ExpressionStatementTree> getUpdate() ;
    @PolyreadThis @Polyread StatementTree getStatement() ;
}
