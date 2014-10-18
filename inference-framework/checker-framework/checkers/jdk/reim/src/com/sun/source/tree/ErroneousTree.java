package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface ErroneousTree extends ExpressionTree {
    @PolyPoly List<? extends Tree> getErrorTrees(@PolyPoly ErroneousTree this) ;
}
