/*
 * Copyright (c) 2003, 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

package com.sun.tools.javac.code;

import com.sun.tools.javac.util.*;

/** A type annotation position.
*
*  <p><b>This is NOT part of any supported API.
*  If you write code that depends on this, you do so at your own risk.
*  This code and its internal interfaces are subject to change or
*  deletion without notice.</b>
*/
// Code duplicated in com.sun.tools.classfile.TypeAnnotation.Position
public class TypeAnnotationPosition implements Cloneable {

    public TargetType type = TargetType.UNKNOWN;

    // For generic/array types.
    public List<Integer> location = List.nil();

    // Tree position.
    public int pos = -1;

    // For typecasts, type tests, new (and locals, as start_pc).
    public boolean isValidOffset = false;
    public int offset = -1;

    // For locals. arrays same length
    public int[] lvarOffset = null;
    public int[] lvarLength = null;
    public int[] lvarIndex = null;

    // For type parameter bound
    public int bound_index = Integer.MIN_VALUE;

    // For type parameter and method parameter
    public int parameter_index = Integer.MIN_VALUE;

    // For class extends, implements, and throws classes
    public int type_index = Integer.MIN_VALUE;

    public TypeAnnotationPosition() { }
    public TypeAnnotationPosition(TargetType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(type);

        switch (type) {
        // type cast
        case TYPECAST:
        case TYPECAST_COMPONENT:
        // instanceof
        case INSTANCEOF:
        case INSTANCEOF_COMPONENT:
        // new expression
        case NEW:
        case NEW_COMPONENT:
            sb.append(", offset = ");
            sb.append(offset);
            break;
        // local variable
        case LOCAL_VARIABLE:
        case LOCAL_VARIABLE_COMPONENT:
            sb.append(", {");
            for (int i = 0; i < lvarOffset.length; ++i) {
                if (i != 0) sb.append("; ");
                sb.append("start_pc = ");
                sb.append(lvarOffset[i]);
                sb.append(", length = ");
                sb.append(lvarLength[i]);
                sb.append(", index = ");
                sb.append(lvarIndex[i]);
            }
            sb.append("}");
            break;
        // method receiver
        case METHOD_RECEIVER:
        case METHOD_RECEIVER_COMPONENT:
            // Do nothing
            break;
        // type parameter
        case CLASS_TYPE_PARAMETER:
        case METHOD_TYPE_PARAMETER:
            sb.append(", param_index = ");
            sb.append(parameter_index);
            break;
        // type parameter bound
        case CLASS_TYPE_PARAMETER_BOUND:
        case CLASS_TYPE_PARAMETER_BOUND_COMPONENT:
        case METHOD_TYPE_PARAMETER_BOUND:
        case METHOD_TYPE_PARAMETER_BOUND_COMPONENT:
            sb.append(", param_index = ");
            sb.append(parameter_index);
            sb.append(", bound_index = ");
            sb.append(bound_index);
            break;
        // class extends or implements clause
        case CLASS_EXTENDS:
        case CLASS_EXTENDS_COMPONENT:
            sb.append(", type_index = ");
            sb.append(type_index);
            break;
        // throws
        case THROWS:
            sb.append(", type_index = ");
            sb.append(type_index);
            break;
        // exception parameter
        case EXCEPTION_PARAMETER:
            // TODO: how do we separate which of the types it is on?
            System.out.println("Handle exception parameters!");
            break;
        // method parameter
        case METHOD_PARAMETER:
        case METHOD_PARAMETER_COMPONENT:
            sb.append(", param_index = ");
            sb.append(parameter_index);
            break;
        // method/constructor type argument
        case NEW_TYPE_ARGUMENT:
        case NEW_TYPE_ARGUMENT_COMPONENT:
        case METHOD_TYPE_ARGUMENT:
        case METHOD_TYPE_ARGUMENT_COMPONENT:
            sb.append(", offset = ");
            sb.append(offset);
            sb.append(", type_index = ");
            sb.append(type_index);
            break;
        // We don't need to worry about these
        case METHOD_RETURN:
        case METHOD_RETURN_COMPONENT:
        case FIELD:
        case FIELD_COMPONENT:
            break;
        case UNKNOWN:
            break;
        default:
            throw new AssertionError("Unknown target type: " + type);
        }

        // Append location data for generics/arrays.
        if (type.hasLocation()) {
            sb.append(", location = (");
            sb.append(location);
            sb.append(")");
        }

        sb.append(", pos = ");
        sb.append(pos);

        sb.append(']');
        return sb.toString();
    }

    /**
     * Indicates whether the target tree of the annotation has been optimized
     * away from classfile or not.
     * @return true if the target has not been optimized away
     */
    public boolean emitToClassfile() {
        return !type.isLocal() || isValidOffset;
    }

    public TypeAnnotationPosition clone() {
        try {
            return (TypeAnnotationPosition)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("This not cloneable");
        }
    }
}
