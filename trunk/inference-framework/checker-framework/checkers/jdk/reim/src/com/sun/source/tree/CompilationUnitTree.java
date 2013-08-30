package com.sun.source.tree;

import java.util.List;
import javax.tools.JavaFileObject;
import com.sun.source.tree.LineMap;
import checkers.inference.reim.quals.*;

public interface CompilationUnitTree extends Tree {
    @Polyread List<? extends AnnotationTree> getPackageAnnotations(@Polyread CompilationUnitTree this) ;
    @Polyread ExpressionTree getPackageName(@Polyread CompilationUnitTree this) ;
    @Polyread List<? extends ImportTree> getImports(@Polyread CompilationUnitTree this) ;
    @Polyread List<? extends Tree> getTypeDecls(@Polyread CompilationUnitTree this) ;
    @Polyread JavaFileObject getSourceFile(@Polyread CompilationUnitTree this) ;
    @Polyread LineMap getLineMap(@Polyread CompilationUnitTree this) ;
}
