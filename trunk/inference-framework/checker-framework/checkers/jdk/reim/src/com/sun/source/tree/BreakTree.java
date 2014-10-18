package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference.reim.quals.*;

public interface BreakTree extends StatementTree {
    @Polyread Name getLabel(@Polyread BreakTree this) ;
}
