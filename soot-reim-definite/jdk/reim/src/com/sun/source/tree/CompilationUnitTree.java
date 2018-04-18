package com.sun.source.tree;

import java.util.List;
import javax.tools.JavaFileObject;
//import com.sun.source.tree.LineMap;
import checkers.inference.reim.quals.*;

public interface CompilationUnitTree extends Tree {
    @PolyreadThis @Polyread List<? extends AnnotationTree> getPackageAnnotations() ;
    @PolyreadThis @Polyread ExpressionTree getPackageName() ;
    @PolyreadThis @Polyread List<? extends ImportTree> getImports() ;
    @PolyreadThis @Polyread List<? extends Tree> getTypeDecls() ;
    @PolyreadThis @Polyread JavaFileObject getSourceFile() ;
    //    @PolyreadThis @Polyread LineMap getLineMap() ;
}
