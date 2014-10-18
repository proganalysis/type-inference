package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference2.reimN.quals.*;

public interface LabeledStatementTree extends StatementTree {
    @PolyPoly Name getLabel(@PolyPoly LabeledStatementTree this) ;
    @PolyPoly StatementTree getStatement(@PolyPoly LabeledStatementTree this) ;
}
