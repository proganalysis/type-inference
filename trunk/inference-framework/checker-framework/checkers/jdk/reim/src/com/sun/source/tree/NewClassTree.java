package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface NewClassTree extends ExpressionTree {
    @Polyread ExpressionTree getEnclosingExpression(@Polyread NewClassTree this) ;
    @Polyread List<? extends Tree> getTypeArguments(@Polyread NewClassTree this) ;
    @Polyread ExpressionTree getIdentifier(@Polyread NewClassTree this) ;
    @Polyread List<? extends ExpressionTree> getArguments(@Polyread NewClassTree this) ;
    @Polyread ClassTree getClassBody(@Polyread NewClassTree this) ;
}
