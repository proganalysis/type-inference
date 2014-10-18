package com.sun.source.tree;
import checkers.inference2.reimN.quals.*;

public interface EnhancedForLoopTree extends StatementTree {
    VariableTree getVariable();
    ExpressionTree getExpression();
    StatementTree getStatement();
}
