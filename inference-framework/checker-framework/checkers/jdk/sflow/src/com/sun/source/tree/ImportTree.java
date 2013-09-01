package com.sun.source.tree;

import checkers.inference.reim.quals.*;

public interface ImportTree extends Tree {
    boolean isStatic(@Readonly ImportTree this) ;
    @Polyread Tree getQualifiedIdentifier(@Polyread ImportTree this) ;
}
