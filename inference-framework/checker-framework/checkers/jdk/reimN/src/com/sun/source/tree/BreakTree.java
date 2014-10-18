package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference2.reimN.quals.*;

public interface BreakTree extends StatementTree {
    @PolyPoly Name getLabel(@PolyPoly BreakTree this) ;
}
