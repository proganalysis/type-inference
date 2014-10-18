package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;

import checkers.inference.reim.quals.*;

public interface TypeParameterTree extends Tree {
    @Polyread Name getName(@Polyread TypeParameterTree this) ;
    @Polyread List<? extends AnnotationTree> getAnnotations(@Polyread TypeParameterTree this) ;
    @Polyread List<? extends Tree> getBounds(@Polyread TypeParameterTree this) ;
}
