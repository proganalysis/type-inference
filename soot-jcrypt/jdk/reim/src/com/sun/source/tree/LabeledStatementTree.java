package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference.reim.quals.*;

public interface LabeledStatementTree extends StatementTree {
    @PolyreadThis @Polyread Name getLabel() ;
    @PolyreadThis @Polyread StatementTree getStatement() ;
}
