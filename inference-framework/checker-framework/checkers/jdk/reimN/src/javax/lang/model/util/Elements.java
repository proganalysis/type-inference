package javax.lang.model.util;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.*;
import javax.lang.model.type.*;

import checkers.inference2.reimN.quals.*;

public interface Elements {
    @PolyPoly PackageElement getPackageElement(@PolyPoly CharSequence name);
    @PolyPoly TypeElement getTypeElement(@PolyPoly CharSequence name);
    @PolyPoly Map<? extends ExecutableElement, ? extends AnnotationValue>
        getElementValuesWithDefaults(@PolyPoly AnnotationMirror a);
    @PolyPoly String getDocComment(@PolyPoly Element e);
    boolean isDeprecated(@ReadRead Element e);
    @PolyPoly Name getBinaryName(@PolyPoly TypeElement type);
    @PolyPoly PackageElement getPackageOf(@PolyPoly Element type);
    @PolyPoly List<? extends Element> getAllMembers(@PolyPoly TypeElement type);
    @PolyPoly List<? extends AnnotationMirror> getAllAnnotationMirrors(@PolyPoly Element e);
    boolean hides(@ReadRead Element hider, @ReadRead Element hidden);
    boolean overrides(@ReadRead ExecutableElement overrider, @ReadRead ExecutableElement overridden,
              @ReadRead TypeElement type);
    @PolyPoly String getConstantExpression(@PolyPoly Object value);
    void printElements(java.io.Writer w, @ReadRead Element... elements);
    @PolyPoly Name getName(@PolyPoly CharSequence cs);
}
