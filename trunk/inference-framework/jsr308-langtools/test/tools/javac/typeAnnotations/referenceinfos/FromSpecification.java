/*
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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

import static com.sun.tools.classfile.TypeAnnotation.TargetType.*;

/*
 * @test
 * @summary Test that the examples from the manual are stored as expected
 * @compile -g Driver.java ReferenceInfoUtil.java FromSpecification.java
 * @run main Driver FromSpecification
 */
public class FromSpecification {

    @TADescriptions({
        @TADescription(annotation = "TA", type = METHOD_PARAMETER, paramIndex = 0),
        @TADescription(annotation = "TB", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {0}, paramIndex = 0),
        @TADescription(annotation = "TC", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {1}, paramIndex = 0),
        @TADescription(annotation = "TD", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {1, 0}, paramIndex = 0)
    })
    public String testSpec1() {
        return "void test(@TA Map<@TB String, @TC List<@TD Object>> a) { }";
    }

    @TADescriptions({
        @TADescription(annotation = "TH", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {2}, paramIndex = 0),
        @TADescription(annotation = "TE", type = METHOD_PARAMETER, paramIndex = 0),
        @TADescription(annotation = "TF", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {0}, paramIndex = 0),
        @TADescription(annotation = "TG", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {1}, paramIndex = 0)
    })
    public String testSpec2() {
        return "void test(@TH String @TE [] @TF [] @TG [] a) { }";
    }

    @TADescriptions({
        @TADescription(annotation = "TL", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {2}, paramIndex = 0),
        @TADescription(annotation = "TK", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {1}, paramIndex = 0),
        @TADescription(annotation = "TJ", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {0}, paramIndex = 0),
        @TADescription(annotation = "TI", type = METHOD_PARAMETER, paramIndex = 0)
    })
    public String testSpec3() {
        return "class Test { class O1 { class O2 { class O3 { class NestedStatic {} } } }" +
                "void test(@TL O1.@TK O2.@TJ O3.@TI NestedStatic a) { } }";
    }

    @TADescriptions({
        @TADescription(annotation = "TA", type = METHOD_PARAMETER, paramIndex = 0),
        @TADescription(annotation = "TB", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {0}, paramIndex = 0),
        @TADescription(annotation = "TC", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {0, 0, 2}, paramIndex = 0),
        @TADescription(annotation = "TD", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {0, 0}, paramIndex = 0),
        @TADescription(annotation = "TE", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {0, 0, 0}, paramIndex = 0),
        @TADescription(annotation = "TF", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {0, 0, 1}, paramIndex = 0),
        @TADescription(annotation = "TG", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {1}, paramIndex = 0),
        @TADescription(annotation = "TH", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {1, 0}, paramIndex = 0)
    })
    public String testSpec4() {
        return "void test(@TA Map<@TB Comparable<@TC Object @TD [] @TE [] @TF []>, @TG List<@TH String>> a) { }";
    }

    @TADescriptions({
        @TADescription(annotation = "TF", type = METHOD_PARAMETER, paramIndex = 0),
        @TADescription(annotation = "TA", type = METHOD_PARAMETER_COMPONENT,
        genericLocation = {4}, paramIndex = 0),
        @TADescription(annotation = "TB", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {3}, paramIndex = 0),
        @TADescription(annotation = "TC", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {3, 0}, paramIndex = 0),
        @TADescription(annotation = "TD", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {3, 1}, paramIndex = 0),
        @TADescription(annotation = "TE", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {2}, paramIndex = 0),
        @TADescription(annotation = "TG", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {0}, paramIndex = 0),
        @TADescription(annotation = "TH", type = METHOD_PARAMETER_COMPONENT,
                genericLocation = {1}, paramIndex = 0)
    })
    public String testSpec5() {
        return "class Test { class O1 { class O2<A, B> { class O3 { class Nested<X, Y> {} } } }" +
                "void test(@TA O1.@TB O2<@TC String, @TD String>.@TE O3.@TF Nested<@TG String, @TH String> a) { } }";
    }
}