package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference.reim.quals.*;

public interface MemberSelectTree extends ExpressionTree {
    @Polyread ExpressionTree getExpression(@Polyread MemberSelectTree this) ;
    @Polyread Name getIdentifier(@Polyread MemberSelectTree this) ;
}
