package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface BlockTree extends StatementTree {
    boolean isStatic(@Readonly BlockTree this) ;
    @Polyread List<? extends StatementTree> getStatements(@Polyread BlockTree this) ;
}
