/*
 * @test /nodynamiccopyright/
 * @bug 1234567
 * @summary A cast cannot consist of only an annotation.
 * @author Werner Dietl
 * @compile/fail/ref=BadCast.out -XDrawDiagnostics -source 1.8 BadCast.java
 */
class BadCast {
  static void main() {
    Object o = (@A) "";
  }
}

@interface A { }
