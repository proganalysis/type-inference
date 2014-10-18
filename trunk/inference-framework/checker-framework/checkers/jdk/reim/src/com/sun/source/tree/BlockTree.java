package com.sun.source.tree;

import java.util.List;
import checkers.inference2.reimN.quals.*;

public interface BlockTree extends StatementTree {
    boolean isStatic(@ReadRead BlockTree this) ;
    @PolyPoly List<? extends StatementTree> getStatements(@PolyPoly BlockTree this) ;
}
