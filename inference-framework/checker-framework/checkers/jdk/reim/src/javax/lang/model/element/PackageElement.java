package javax.lang.model.element;

import checkers.inference.reim.quals.*;

public interface PackageElement extends Element, QualifiedNameable {
    @Polyread Name getQualifiedName(@Polyread PackageElement this) ;
    boolean isUnnamed(@Readonly PackageElement this) ;
}
