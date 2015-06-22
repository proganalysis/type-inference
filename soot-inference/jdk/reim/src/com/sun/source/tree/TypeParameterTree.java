package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;

import checkers.inference.reim.quals.*;

public interface TypeParameterTree extends Tree {
    @PolyreadThis @Polyread Name getName() ;
    @PolyreadThis @Polyread List<? extends AnnotationTree> getAnnotations() ;
    @PolyreadThis @Polyread List<? extends Tree> getBounds() ;
}
