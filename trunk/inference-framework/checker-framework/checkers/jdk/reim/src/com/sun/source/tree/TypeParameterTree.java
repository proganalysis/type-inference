package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;

import checkers.inference2.reimN.quals.*;

public interface TypeParameterTree extends Tree {
    @PolyPoly Name getName(@PolyPoly TypeParameterTree this) ;
    @PolyPoly List<? extends AnnotationTree> getAnnotations(@PolyPoly TypeParameterTree this) ;
    @PolyPoly List<? extends Tree> getBounds(@PolyPoly TypeParameterTree this) ;
}
