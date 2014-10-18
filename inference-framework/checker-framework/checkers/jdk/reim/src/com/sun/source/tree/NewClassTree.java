package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface NewClassTree extends ExpressionTree {
    @PolyPoly ExpressionTree getEnclosingExpression(@PolyPoly NewClassTree this) ;
    @PolyPoly List<? extends Tree> getTypeArguments(@PolyPoly NewClassTree this) ;
    @PolyPoly ExpressionTree getIdentifier(@PolyPoly NewClassTree this) ;
    @PolyPoly List<? extends ExpressionTree> getArguments(@PolyPoly NewClassTree this) ;
    @PolyPoly ClassTree getClassBody(@PolyPoly NewClassTree this) ;
}
