package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface BlockTree extends StatementTree {
    @ReadonlyThis boolean isStatic() ;
    @PolyreadThis @Polyread List<? extends StatementTree> getStatements() ;
}
