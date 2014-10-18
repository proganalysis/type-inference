package com.sun.source.tree;

import java.util.List;
import javax.tools.JavaFileObject;
import com.sun.source.tree.LineMap;
import checkers.inference2.reimN.quals.*;

public interface CompilationUnitTree extends Tree {
    @PolyPoly List<? extends AnnotationTree> getPackageAnnotations(@PolyPoly CompilationUnitTree this) ;
    @PolyPoly ExpressionTree getPackageName(@PolyPoly CompilationUnitTree this) ;
    @PolyPoly List<? extends ImportTree> getImports(@PolyPoly CompilationUnitTree this) ;
    @PolyPoly List<? extends Tree> getTypeDecls(@PolyPoly CompilationUnitTree this) ;
    @PolyPoly JavaFileObject getSourceFile(@PolyPoly CompilationUnitTree this) ;
    @PolyPoly LineMap getLineMap(@PolyPoly CompilationUnitTree this) ;
}
