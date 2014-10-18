package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface ForLoopTree extends StatementTree {
    @Polyread List<? extends StatementTree> getInitializer(@Polyread ForLoopTree this) ;
    @Polyread ExpressionTree getCondition(@Polyread ForLoopTree this) ;
    @Polyread List<? extends ExpressionStatementTree> getUpdate(@Polyread ForLoopTree this) ;
    @Polyread StatementTree getStatement(@Polyread ForLoopTree this) ;
}
