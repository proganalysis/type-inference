/*
 * @test /nodynamiccopyright/
 * @bug 1234567
 * @summary Package declarations cannot use annotations.
 * @author Werner Dietl
 * @compile/fail/ref=AnnotatedPackage2.out -XDrawDiagnostics -source 1.8 AnnotatedPackage2.java
 */

package @A p1.p2;

class AnnotatedPackage2 { }

@interface A { }
