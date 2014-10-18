package javax.lang.model.element;

import checkers.inference2.reimN.quals.*;

public interface PackageElement extends Element, QualifiedNameable {
    @PolyPoly Name getQualifiedName(@PolyPoly PackageElement this) ;
    boolean isUnnamed(@ReadRead PackageElement this) ;
}
