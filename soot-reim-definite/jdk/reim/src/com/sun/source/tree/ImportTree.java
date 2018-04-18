package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ImportTree extends Tree {
    @ReadonlyThis boolean isStatic() ;
    @PolyreadThis @Polyread Tree getQualifiedIdentifier() ;
}
