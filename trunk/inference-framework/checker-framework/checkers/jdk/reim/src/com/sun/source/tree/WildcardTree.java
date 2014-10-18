package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface WildcardTree extends Tree {
    @Polyread Tree getBound(@Polyread WildcardTree this) ;
}
