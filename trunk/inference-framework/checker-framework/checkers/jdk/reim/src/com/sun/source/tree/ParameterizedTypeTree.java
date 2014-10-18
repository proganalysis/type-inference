package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface ParameterizedTypeTree extends Tree {
    @PolyPoly Tree getType(@PolyPoly ParameterizedTypeTree this) ;
    @PolyPoly List<? extends Tree> getTypeArguments(@PolyPoly ParameterizedTypeTree this) ;
}
