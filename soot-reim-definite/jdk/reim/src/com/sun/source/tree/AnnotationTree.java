package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface AnnotationTree extends ExpressionTree {
    @PolyreadThis @Polyread Tree getAnnotationType() ;
    @PolyreadThis @Polyread List<? extends ExpressionTree> getArguments() ;
}
