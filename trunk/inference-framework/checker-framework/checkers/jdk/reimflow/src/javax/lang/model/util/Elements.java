package javax.lang.model.util;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.*;
import javax.lang.model.type.*;

import checkers.inference.reim.quals.*;

public interface Elements {
    @Polyread PackageElement getPackageElement(@Polyread CharSequence name);
    @Polyread TypeElement getTypeElement(@Polyread CharSequence name);
    @Polyread Map<? extends ExecutableElement, ? extends AnnotationValue>
        getElementValuesWithDefaults(@Polyread AnnotationMirror a);
    @Polyread String getDocComment(@Polyread Element e);
    boolean isDeprecated(@Readonly Element e);
    @Polyread Name getBinaryName(@Polyread TypeElement type);
    @Polyread PackageElement getPackageOf(@Polyread Element type);
    @Polyread List<? extends Element> getAllMembers(@Polyread TypeElement type);
    @Polyread List<? extends AnnotationMirror> getAllAnnotationMirrors(@Polyread Element e);
    boolean hides(@Readonly Element hider, @Readonly Element hidden);
    boolean overrides(@Readonly ExecutableElement overrider, @Readonly ExecutableElement overridden,
              @Readonly TypeElement type);
    @Polyread String getConstantExpression(@Polyread Object value);
    void printElements(java.io.Writer w, @Readonly Element... elements);
    @Polyread Name getName(@Polyread CharSequence cs);
}
