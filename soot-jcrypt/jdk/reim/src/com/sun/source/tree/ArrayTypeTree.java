package com.sun.source.tree;
import checkers.inference.reim.quals.*;

public interface ArrayTypeTree extends Tree {
    @PolyreadThis @Polyread Tree getType() ;
}
