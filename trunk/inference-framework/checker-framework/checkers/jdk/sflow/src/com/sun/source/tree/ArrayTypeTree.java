package com.sun.source.tree;
import checkers.inference.reim.quals.*;

public interface ArrayTypeTree extends Tree {
    @Polyread Tree getType(@Polyread ArrayTypeTree this) ;
}
