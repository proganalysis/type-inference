/*
 * @test /nodynamiccopyright/
 * @bug 1234567
 * @summary Package declarations cannot use annotations.
 * @author Werner Dietl
 * @compile/fail/ref=AnnotatedPackage1.out -XDrawDiagnostics -source 1.8 AnnotatedPackage1.java
 */

package name.@A p1.p2;

class AnnotatedPackage1 { }

@interface A { }
