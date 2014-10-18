package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface WildcardTree extends Tree {
    @PolyPoly Tree getBound(@PolyPoly WildcardTree this) ;
}
