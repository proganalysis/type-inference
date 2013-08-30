/*
 * @test /nodynamiccopyright/
 * @bug 6843077
 * @summary check for duplicate annotations
 * @author Mahmood Ali
 * @compile/fail/ref=DuplicateTypeAnnotation.out -XDrawDiagnostics -source 1.8 DuplicateTypeAnnotation.java
 */
import java.lang.annotation.*;
class DuplicateTypeAnnotation {
  void test() {
    String @A @A [] s;
  }
}

@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface A { }
