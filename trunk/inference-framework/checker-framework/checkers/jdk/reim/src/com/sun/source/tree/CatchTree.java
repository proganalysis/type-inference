package com.sun.source.tree;

import checkers.inference2.reimN.quals.*;

public interface CatchTree extends Tree {
    @PolyPoly VariableTree getParameter(@PolyPoly CatchTree this) ;
    @PolyPoly BlockTree getBlock(@PolyPoly CatchTree this) ;
}
