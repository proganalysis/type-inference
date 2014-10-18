package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference.reim.quals.*;

public interface IdentifierTree extends ExpressionTree {
    @Polyread Name getName(@Polyread IdentifierTree this) ;
}
