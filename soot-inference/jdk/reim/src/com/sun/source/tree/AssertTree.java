package com.sun.source.tree;
import checkers.inference.reim.quals.*;

public interface AssertTree extends StatementTree {
    @PolyreadThis @Polyread ExpressionTree getCondition() ;
    @PolyreadThis @Polyread ExpressionTree getDetail() ;
}
