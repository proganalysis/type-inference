/*
 * @test /nodynamiccopyright/
 * @bug 6843077
 * @summary check for duplicate annotations in receiver
 * @author Mahmood Ali
 * @compile/fail/ref=DuplicateTypeAnnotation.out -XDrawDiagnostics -source 1.8 DuplicateTypeAnnotation.java
 */
import java.lang.annotation.*;
class DuplicateTypeAnnotation {
  void test(@A @A DuplicateTypeAnnotation this) { }
}

@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface A { }
