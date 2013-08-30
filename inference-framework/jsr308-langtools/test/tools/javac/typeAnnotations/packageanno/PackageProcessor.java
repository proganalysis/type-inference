/*
 * Copyright (c) 2009 Oracle and/or its affiliates. All rights reserved.
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
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import com.sun.source.util.AbstractTypeProcessor;
import com.sun.source.util.TreePath;

/*
 * @test
 * @summary test that package annotations are available to type processors
 * @author Mahmood Ali
 * @compile -source 1.8 PackageProcessor.java
 * @compile -source 1.8 -cp . -processor PackageProcessor mypackage/Anno.java mypackage/MyClass.java mypackage/package-info.java
 */

@SupportedAnnotationTypes("*")
public class PackageProcessor extends AbstractTypeProcessor {

    @Override
    public void typeProcess(TypeElement element, TreePath tree) {
        if (element.getSimpleName().contentEquals("MyClass")) {
            Element owner = element.getEnclosingElement();
            if (owner.getKind() != ElementKind.PACKAGE)
                throw new RuntimeException("class owner should be a package: " + owner);
            if (owner.getAnnotationMirrors().size() != 1)
                throw new RuntimeException("the owner package should have one annotation: " + owner);
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
