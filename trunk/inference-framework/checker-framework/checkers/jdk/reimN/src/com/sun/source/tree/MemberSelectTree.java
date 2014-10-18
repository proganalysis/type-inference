package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference2.reimN.quals.*;

public interface MemberSelectTree extends ExpressionTree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly MemberSelectTree this) ;
    @PolyPoly Name getIdentifier(@PolyPoly MemberSelectTree this) ;
}
