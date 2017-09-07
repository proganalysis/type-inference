package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface TryTree extends StatementTree {
    @PolyreadThis @Polyread BlockTree getBlock() ;
    @PolyreadThis @Polyread List<? extends CatchTree> getCatches() ;
    @PolyreadThis @Polyread BlockTree getFinallyBlock() ;
}
