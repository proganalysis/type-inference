package com.sun.source.tree;
import checkers.inference.reim.quals.*;

public interface EnhancedForLoopTree extends StatementTree {
    VariableTree getVariable();
    ExpressionTree getExpression();
    StatementTree getStatement();
}
