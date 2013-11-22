package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface AnnotationTree extends ExpressionTree {
    @Polyread Tree getAnnotationType(@Polyread AnnotationTree this) ;
    @Polyread List<? extends ExpressionTree> getArguments(@Polyread AnnotationTree this) ;
}
