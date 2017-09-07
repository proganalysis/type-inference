package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference.reim.quals.*;

public interface MemberSelectTree extends ExpressionTree {
    @PolyreadThis @Polyread ExpressionTree getExpression() ;
    @PolyreadThis @Polyread Name getIdentifier() ;
}
