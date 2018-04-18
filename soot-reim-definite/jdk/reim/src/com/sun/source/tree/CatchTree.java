package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface CatchTree extends Tree {
    @PolyreadThis @Polyread VariableTree getParameter() ;
    @PolyreadThis @Polyread BlockTree getBlock() ;
}
