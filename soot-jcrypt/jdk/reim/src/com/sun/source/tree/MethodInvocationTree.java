package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface MethodInvocationTree extends ExpressionTree {
    @PolyreadThis @Polyread List<? extends Tree> getTypeArguments() ;
    @PolyreadThis @Polyread ExpressionTree getMethodSelect() ;
    @PolyreadThis @Polyread List<? extends ExpressionTree> getArguments() ;
}
