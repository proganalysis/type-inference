package com.sun.source.tree;

import java.util.List;
import checkers.inference.reim.quals.*;

public interface ParameterizedTypeTree extends Tree {
    @Polyread Tree getType(@Polyread ParameterizedTypeTree this) ;
    @Polyread List<? extends Tree> getTypeArguments(@Polyread ParameterizedTypeTree this) ;
}
