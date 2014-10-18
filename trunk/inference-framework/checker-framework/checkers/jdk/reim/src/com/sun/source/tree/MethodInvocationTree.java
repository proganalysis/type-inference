package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface MethodInvocationTree extends ExpressionTree {
    @PolyPoly List<? extends Tree> getTypeArguments(@PolyPoly MethodInvocationTree this) ;
    @PolyPoly ExpressionTree getMethodSelect(@PolyPoly MethodInvocationTree this) ;
    @PolyPoly List<? extends ExpressionTree> getArguments(@PolyPoly MethodInvocationTree this) ;
}
