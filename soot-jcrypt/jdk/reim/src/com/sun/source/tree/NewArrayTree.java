package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface NewArrayTree extends ExpressionTree {
    @PolyreadThis @Polyread Tree getType() ;
    @PolyreadThis @Polyread List<? extends ExpressionTree> getDimensions() ;
    @PolyreadThis @Polyread List<? extends ExpressionTree> getInitializers() ;
}
