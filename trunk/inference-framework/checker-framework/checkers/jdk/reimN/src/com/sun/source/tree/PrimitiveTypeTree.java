package com.sun.source.tree;

import javax.lang.model.type.TypeKind;
import checkers.inference2.reimN.quals.*;

public interface PrimitiveTypeTree extends Tree {
    @PolyPoly TypeKind getPrimitiveTypeKind(@PolyPoly PrimitiveTypeTree this) ;
}
