package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface NewArrayTree extends ExpressionTree {
    @PolyPoly Tree getType(@PolyPoly NewArrayTree this) ;
    @PolyPoly List<? extends ExpressionTree> getDimensions(@PolyPoly NewArrayTree this) ;
    @PolyPoly List<? extends ExpressionTree> getInitializers(@PolyPoly NewArrayTree this) ;
}
