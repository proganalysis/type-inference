package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference2.reimN.quals.*;

public interface IdentifierTree extends ExpressionTree {
    @PolyPoly Name getName(@PolyPoly IdentifierTree this) ;
}
