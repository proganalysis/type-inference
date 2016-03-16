package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface WildcardTree extends Tree {
    @PolyreadThis @Polyread Tree getBound() ;
}
