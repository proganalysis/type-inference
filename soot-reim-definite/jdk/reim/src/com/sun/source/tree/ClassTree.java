package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;
import checkers.inference.reim.quals.*;

public interface ClassTree extends StatementTree {
    @PolyreadThis @Polyread ModifiersTree getModifiers() ;
    @PolyreadThis @Polyread Name getSimpleName() ;
    @PolyreadThis @Polyread List<? extends TypeParameterTree> getTypeParameters() ;
    @PolyreadThis @Polyread Tree getExtendsClause() ;
    @PolyreadThis @Polyread List<? extends Tree> getImplementsClause() ;
    @PolyreadThis @Polyread List<? extends Tree> getMembers() ;
}
