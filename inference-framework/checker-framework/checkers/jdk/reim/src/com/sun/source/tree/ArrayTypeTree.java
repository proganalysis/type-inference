package com.sun.source.tree;
import checkers.inference2.reimN.quals.*;

public interface ArrayTypeTree extends Tree {
    @PolyPoly Tree getType(@PolyPoly ArrayTypeTree this) ;
}
