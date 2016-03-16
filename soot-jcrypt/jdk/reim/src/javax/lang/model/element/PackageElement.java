package javax.lang.model.element;

import checkers.inference.reim.quals.*;

public interface PackageElement extends Element, QualifiedNameable {
    @PolyreadThis @Polyread Name getQualifiedName() ;
    @ReadonlyThis boolean isUnnamed() ;
}
