package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface ImportTree extends Tree {
    boolean isStatic(@ReadRead ImportTree this) ;
    @PolyPoly Tree getQualifiedIdentifier(@PolyPoly ImportTree this) ;
}
