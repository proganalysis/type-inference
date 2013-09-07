package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface CatchTree extends Tree {
    @Polyread VariableTree getParameter(@Polyread CatchTree this) ;
    @Polyread BlockTree getBlock(@Polyread CatchTree this) ;
}
