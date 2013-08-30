/*
 * Copyright (c) 2008, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import java.lang.annotation.*;
import java.util.Map;

/*
 * @test
 * @bug 1234567
 * @summary new type annotation location: nested types
 * @author Werner Dietl
 * @compile -source 1.8 NestedTypes.java
 */
class Outer {
    class Inner {
        class Inner2 {
            // m1a-c all have the same parameter type.
            void m1a(@A Inner2 p1a) {}
            void m1b(Inner.@A Inner2 p1b) {}
            void m1c(Outer.Inner.@A Inner2 p1c) {}
            // notice the difference to m1d
            void m1d(@A Outer.Inner.Inner2 p1d) {}

            // m2a-b both have the same parameter type.
            void m2a(@A Inner.Inner2 p2a) {}
            void m2b(Outer.@A Inner.Inner2 p2b) {}

            // The location for @A is different in m3a-c 
            void m3a(@A Outer p3a) {} // no location
            void m3b(@A Outer.Inner p3b) {} // location [0]
            void m3c(@A Outer.Inner.Inner2 p3c) {} // location [1]
        }
    }

    void m4a(@A Map p4a) {} // no location
    void m4b(@A Map.Entry p4b) {} // location [0]
    void m4c(@A Map.@B Entry p4c) {} // @A location [0]
    void m4d(@A Map<String,String>.@B Entry<String,String> p4d) {} // @A location [2]
    void m4e(MyList<@A Map.Entry> p4e) {} // @A location [0,0]
    void m4f(MyList<@A Map.@B Entry> p4f) {} // @A location [0,0]

    class GInner<X> {
        class GInner2<Y, Z> {}
    }

    static class Static {}
    static class GStatic<X, Y> {
        static class GStatic2<Z> {}
    }
}

class Test1 {
    // Outer.GStatic<Object,Object>.GStatic2<Object> gs;
    Outer.GStatic.@A GStatic2<Object> gsgood;
    // TODO: add failing test
    // Outer.@A GStatic.GStatic2<Object> gsbad;

    MyList<@A Outer . @B Inner. @C Inner2> f;
    // @A location: [4]
    @A Outer .GInner<Object>.GInner2<String, Integer> g;

    // TODO: Put @A on the type, not the package, maybe.
    //MyList<@A java.lang.Object> pkg;

    // Make sure that something like this fails gracefully: 
    // MyList<java.@B lang.Object> pkg;

    @A Outer f1;
    @A Outer . @B Inner f2 = f1.new @B Inner();
    // TODO: ensure type annos on new are stored.
    @A Outer . @B GInner<@C Object> f3 = f1.new @B GInner<@C Object>();

    // @A location [0, 3]
    // @B location [0, 2]
    // @C location [0, 2, 0]
    // @D location [0, 2, 0, 0]
    // @E location [0] 
    // @F location [0, 0]
    // @G location [0, 1]
    MyList<@A Outer . @B GInner<@C MyList<@D Object>>. @E GInner2<@F Integer, @G Object>> f4;
    // MyList<Outer.GInner<Object>.GInner2<Integer>> f4clean;

    // @A location [3]
    // @B location [2]
    // @C location [2, 0]
    // @D location [2, 0, 0]
    // @E on field 
    // @F location [0]
    // @G location [1]
    @A Outer . @B GInner<@C MyList<@D Object>>. @E GInner2<@F Integer, @G Object> f4top;

    // @A location [0, 1, 3]
    // @B location [0, 1, 2]
    // @C location [0, 1, 2, 0]
    // @D location [0, 1, 2, 0, 0, 1]
    // @E location [0, 1, 2, 0, 0]
    // @F location [0, 1, 2, 0, 0, 0]
    // @G location [0, 1]
    // @H location [0, 1, 0]
    // @I location [0, 1, 1]
    // @J location [0]
    // @K location [0, 0]
    MyList<@A Outer . @B GInner<@C MyList<@D Object @E[] @F[]>>. @G GInner2<@H Integer, @I Object> @J[] @K[]> f4arr;

    // @A location [1, 3]
    // @B location [1, 2]
    // @C location [1, 2, 0]
    // @D location [1, 2, 0, 0, 1]
    // @E location [1, 2, 0, 0]
    // @F location [1, 2, 0, 0, 0]
    // @G location [1]
    // @H location [1, 0]
    // @I location [1, 1]
    // @J on field
    // @K location [0]
    @A Outer . @B GInner<@C MyList<@D Object @E[] @F[]>>. @G GInner2<@H Integer, @I Object> @J[] @K[] f4arrtop;

    MyList<@A Outer . @B Static> f5;
    @A Outer . @B Static f6;
    @Av("A") Outer . @Bv("B") GStatic<@Cv("C") String, @Dv("D") Object> f7;
    @A Outer . @Cv("Data") Static f8;
    MyList<@A Outer . @Cv("Data") Static> f9;
}

class Test2 {
    void m() {
        @A Outer f1 = null;
        @A Outer.@B Inner f2 = null;
        @A Outer.@B Static f3 = null;
        @A Outer.@C Inner f4 = null;

        @A Outer . @B Static f5 = null;
        @A Outer . @Cv("Data") Static f6 = null;
        MyList<@A Outer . @Cv("Data") Static> f7 = null;
    }
}

class Test3 {
    void monster(@A Outer p1,
        @A Outer.@B Inner p2,
        @A Outer.@B Static p3,
        @A Outer.@Cv("Test") Inner p4,
        @A Outer . @B Static p5,
        @A Outer . @Cv("Data") Static p6,
        MyList<@A Outer . @Cv("Data") Static> p7) {
    }
}

class Test4 {
    void m() {
        @A Outer p1 = new @A Outer();
        @A Outer.@B Inner p2 = p1.new @B Inner();
        @A Outer.@B Static p3 = new @A Outer.@B Static();
        @A Outer.@Cv("Test") Inner p4 = p1.new @Cv("Test") Inner();
        @A Outer . @B Static p5 = new @A Outer . @B Static();
        @A Outer . @Cv("Data") Static p6 = new @A Outer . @Cv("Data") Static();
        MyList<@A Outer . @Cv("Data") Static> p7 = new MyList<@A Outer . @Cv("Data") Static>();
    }
}

class MyList<K> { }


@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface A { }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface B { }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface C { }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface D { }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface E { }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface F { }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface G { }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface H { }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface I { }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface J { }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface K { }

@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface Av { String value(); }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface Bv { String value(); }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface Cv { String value(); }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface Dv { String value(); }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface Ev { String value(); }
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@interface Fv { String value(); }

