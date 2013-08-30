package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface MethodInvocationTree extends ExpressionTree {
    @Polyread List<? extends Tree> getTypeArguments(@Polyread MethodInvocationTree this) ;
    @Polyread ExpressionTree getMethodSelect(@Polyread MethodInvocationTree this) ;
    @Polyread List<? extends ExpressionTree> getArguments(@Polyread MethodInvocationTree this) ;
}
