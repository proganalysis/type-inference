package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface NewArrayTree extends ExpressionTree {
    @Polyread Tree getType(@Polyread NewArrayTree this) ;
    @Polyread List<? extends ExpressionTree> getDimensions(@Polyread NewArrayTree this) ;
    @Polyread List<? extends ExpressionTree> getInitializers(@Polyread NewArrayTree this) ;
}
