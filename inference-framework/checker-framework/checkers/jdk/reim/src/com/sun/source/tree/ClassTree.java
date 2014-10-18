package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;
import checkers.inference2.reimN.quals.*;

public interface ClassTree extends StatementTree {
    @PolyPoly ModifiersTree getModifiers(@PolyPoly ClassTree this) ;
    @PolyPoly Name getSimpleName(@PolyPoly ClassTree this) ;
    @PolyPoly List<? extends TypeParameterTree> getTypeParameters(@PolyPoly ClassTree this) ;
    @PolyPoly Tree getExtendsClause(@PolyPoly ClassTree this) ;
    @PolyPoly List<? extends Tree> getImplementsClause(@PolyPoly ClassTree this) ;
    @PolyPoly List<? extends Tree> getMembers(@PolyPoly ClassTree this) ;
}
