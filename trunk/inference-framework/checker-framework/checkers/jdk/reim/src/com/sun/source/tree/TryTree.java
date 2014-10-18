package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface TryTree extends StatementTree {
    @PolyPoly BlockTree getBlock(@PolyPoly TryTree this) ;
    @PolyPoly List<? extends CatchTree> getCatches(@PolyPoly TryTree this) ;
    @PolyPoly BlockTree getFinallyBlock(@PolyPoly TryTree this) ;
}
