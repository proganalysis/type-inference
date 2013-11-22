package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference.reim.quals.*;

public interface LabeledStatementTree extends StatementTree {
    @Polyread Name getLabel(@Polyread LabeledStatementTree this) ;
    @Polyread StatementTree getStatement(@Polyread LabeledStatementTree this) ;
}
