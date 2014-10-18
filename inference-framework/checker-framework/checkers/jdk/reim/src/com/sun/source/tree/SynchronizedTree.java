package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface SynchronizedTree extends StatementTree {
    @PolyPoly ExpressionTree getExpression(@PolyPoly SynchronizedTree this) ;
    @PolyPoly BlockTree getBlock(@PolyPoly SynchronizedTree this) ;
}
