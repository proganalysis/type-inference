package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface AnnotationTree extends ExpressionTree {
    @PolyPoly Tree getAnnotationType(@PolyPoly AnnotationTree this) ;
    @PolyPoly List<? extends ExpressionTree> getArguments(@PolyPoly AnnotationTree this) ;
}
