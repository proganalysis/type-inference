package com.sun.source.tree;

import javax.lang.model.element.Name;
import checkers.inference2.reimN.quals.*;

public interface ContinueTree extends StatementTree {
    @PolyPoly Name getLabel(@PolyPoly ContinueTree this) ;
}
