package com.sun.source.tree;

import javax.lang.model.type.TypeKind;
import checkers.inference.reim.quals.*;

public interface PrimitiveTypeTree extends Tree {
    @Polyread TypeKind getPrimitiveTypeKind(@Polyread PrimitiveTypeTree this) ;
}
