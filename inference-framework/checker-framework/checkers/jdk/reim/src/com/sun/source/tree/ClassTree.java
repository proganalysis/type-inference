package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;
import checkers.inference.reim.quals.*;

public interface ClassTree extends StatementTree {
    @Polyread ModifiersTree getModifiers(@Polyread ClassTree this) ;
    @Polyread Name getSimpleName(@Polyread ClassTree this) ;
    @Polyread List<? extends TypeParameterTree> getTypeParameters(@Polyread ClassTree this) ;
    @Polyread Tree getExtendsClause(@Polyread ClassTree this) ;
    @Polyread List<? extends Tree> getImplementsClause(@Polyread ClassTree this) ;
    @Polyread List<? extends Tree> getMembers(@Polyread ClassTree this) ;
}
