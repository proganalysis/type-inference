package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface ParameterizedTypeTree extends Tree {
    @PolyreadThis @Polyread Tree getType() ;
    @PolyreadThis @Polyread List<? extends Tree> getTypeArguments() ;
}
