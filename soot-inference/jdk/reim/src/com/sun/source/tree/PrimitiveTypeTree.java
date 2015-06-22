package com.sun.source.tree;

import javax.lang.model.type.TypeKind;
import checkers.inference.reim.quals.*;

public interface PrimitiveTypeTree extends Tree {
    @PolyreadThis @Polyread TypeKind getPrimitiveTypeKind() ;
}
