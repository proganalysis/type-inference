package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface TryTree extends StatementTree {
    @Polyread BlockTree getBlock(@Polyread TryTree this) ;
    @Polyread List<? extends CatchTree> getCatches(@Polyread TryTree this) ;
    @Polyread BlockTree getFinallyBlock(@Polyread TryTree this) ;
}
