package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface ErroneousTree extends ExpressionTree {
    @Polyread List<? extends Tree> getErrorTrees(@Polyread ErroneousTree this) ;
}
