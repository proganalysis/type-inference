/*
 * @test /nodynamiccopyright/
 * @bug 6843077
 * @summary static methods don't have receivers
 * @author Mahmood Ali
 * @compile/fail/ref=StaticMethods.out -XDrawDiagnostics -source 1.8 StaticMethods.java
 */
class StaticMethods {
  static void main(@A StaticMethods this) { }
}

@interface A { }
