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
import java.io.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import com.sun.source.util.AbstractTypeProcessor;
import com.sun.source.util.TreePath;

/*
 * @test
 * @summary test that type processors are run when -proc:only is passed
 * @author Mahmood Ali
 */

@SupportedAnnotationTypes("*")
public class TypeProcOnly extends AbstractTypeProcessor {
    private static final String INDICATOR = "PASSED";

    @Override
    public void typeProcess(TypeElement element, TreePath tree) {
        System.out.println(INDICATOR);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    private static File writeTestFile() throws IOException {
        File f = new File("Test.java");
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
        out.println("class Test { }");
        out.close();
        return f;
    }

    public static void main(String[] args) throws Exception {
        PrintStream prevOut = System.out;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);
        System.setOut(out);

        try {
            File f = writeTestFile();
            com.sun.tools.javac.Main.compile(new String[] {"-proc:only", "-processor", "TypeProcOnly", f.getAbsolutePath()});
        } finally {
            System.setOut(prevOut);
        }

        if (bytes.toString().trim().equals(INDICATOR)) {
            System.out.println("PASSED");
        } else {
            throw new Exception("Processor did not run correctly. Output: " + bytes);
        }
    }
}
