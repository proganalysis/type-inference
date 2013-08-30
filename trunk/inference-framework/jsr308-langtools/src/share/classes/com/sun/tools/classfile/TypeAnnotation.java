/*
 * Copyright (c) 2009, 2010, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.tools.classfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.sun.tools.classfile.TypeAnnotation.TargetAttribute.*;

/**
 * See JSR 308 specification, Section 3.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class TypeAnnotation {
    TypeAnnotation(ClassReader cr) throws IOException, Annotation.InvalidAnnotation {
        constant_pool = cr.getConstantPool();
        annotation = new Annotation(cr);
        position = read_position(cr);
    }

    public TypeAnnotation(ConstantPool constant_pool,
            Annotation annotation, Position position) {
        this.constant_pool = constant_pool;
        this.annotation = annotation;
        this.position = position;
    }

    public int length() {
        int n = annotation.length();
        n += position_length(position);
        return n;
    }

    @Override
    public String toString() {
        try {
            return "@" + constant_pool.getUTF8Value(annotation.type_index).toString().substring(1) +
                    " pos: " + position.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public final ConstantPool constant_pool;
    public final Annotation annotation;
    public final Position position;

    private static Position read_position(ClassReader cr) throws IOException, Annotation.InvalidAnnotation {
        // Copied from ClassReader
        int tag = (char)cr.readUnsignedShort();  // cast to introduce signedness
        if (!TargetType.isValidTargetTypeValue(tag))
            throw new Annotation.InvalidAnnotation("invalid type annotation target type value: " + tag);

        TargetType type = TargetType.fromTargetTypeValue(tag);

        Position position = new Position();
        position.type = type;

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
            position.offset = cr.readUnsignedShort();
            break;
        // local variable
        case LOCAL_VARIABLE:
        case LOCAL_VARIABLE_COMPONENT:
            int table_length = cr.readUnsignedShort();
            position.lvarOffset = new int[table_length];
            position.lvarLength = new int[table_length];
            position.lvarIndex = new int[table_length];
            for (int i = 0; i < table_length; ++i) {
                position.lvarOffset[i] = cr.readUnsignedShort();
                position.lvarLength[i] = cr.readUnsignedShort();
                position.lvarIndex[i] = cr.readUnsignedShort();
            }
            break;
        // method receiver
        case METHOD_RECEIVER:
        case METHOD_RECEIVER_COMPONENT:
            // Do nothing
            break;
        // type parameter
        case CLASS_TYPE_PARAMETER:
        case METHOD_TYPE_PARAMETER:
            position.parameter_index = cr.readUnsignedByte();
            break;
        // type parameter bound
        case CLASS_TYPE_PARAMETER_BOUND:
        case CLASS_TYPE_PARAMETER_BOUND_COMPONENT:
        case METHOD_TYPE_PARAMETER_BOUND:
        case METHOD_TYPE_PARAMETER_BOUND_COMPONENT:
            position.parameter_index = cr.readUnsignedByte();
            position.bound_index = cr.readUnsignedByte();
            break;
        case CLASS_EXTENDS:
        case CLASS_EXTENDS_COMPONENT:
            int in = cr.readUnsignedShort();
            if (in == 0xFFFF)
                in = -1;
            position.type_index = in;
            break;
        // throws
        case THROWS:
            position.type_index = cr.readUnsignedShort();
            break;
        // exception parameter
        case EXCEPTION_PARAMETER:
            // TODO: how do we separate which of the types it is on?
            System.out.println("Handle exception parameters!");
            break;
        // method parameter
        case METHOD_PARAMETER:
        case METHOD_PARAMETER_COMPONENT:
            position.parameter_index = cr.readUnsignedByte();
            break;
        // method/constructor type argument
        case NEW_TYPE_ARGUMENT:
        case NEW_TYPE_ARGUMENT_COMPONENT:
        case METHOD_TYPE_ARGUMENT:
        case METHOD_TYPE_ARGUMENT_COMPONENT:
            position.offset = cr.readUnsignedShort();
            position.type_index = cr.readUnsignedByte();
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

        if (type.hasLocation()) {
            int len = cr.readUnsignedShort();
            List<Integer> loc = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++)
                loc.add(cr.readUnsignedByte());
            position.location = loc;
        }
        return position;
    }

    private static int position_length(Position pos) {
        int n = 0;
        n += 2; // target_type
        switch (pos.type) {
        // type cast
        case TYPECAST:
        case TYPECAST_COMPONENT:
        // instanceof
        case INSTANCEOF:
        case INSTANCEOF_COMPONENT:
        // new expression
        case NEW:
        case NEW_COMPONENT:
            n += 2;
            break;
        // local variable
        case LOCAL_VARIABLE:
        case LOCAL_VARIABLE_COMPONENT:
            n += 2; // table_length;
            int table_length = pos.lvarOffset.length;
            n += 2 * table_length; // offset
            n += 2 * table_length; // length;
            n += 2 * table_length; // index
            break;
        // method receiver
        case METHOD_RECEIVER:
        case METHOD_RECEIVER_COMPONENT:
            // Do nothing
            break;
        // type parameter
        case CLASS_TYPE_PARAMETER:
        case METHOD_TYPE_PARAMETER:
            n += 1; // parameter_index;
            break;
        // type parameter bound
        case CLASS_TYPE_PARAMETER_BOUND:
        case CLASS_TYPE_PARAMETER_BOUND_COMPONENT:
        case METHOD_TYPE_PARAMETER_BOUND:
        case METHOD_TYPE_PARAMETER_BOUND_COMPONENT:
            n += 1; // parameter_index
            n += 1; // bound_index
            break;
        // class extends or implements clause
        case CLASS_EXTENDS:
        case CLASS_EXTENDS_COMPONENT:
            n += 2; // type_index
            break;
        // throws
        case THROWS:
            n += 2; // type_index
            break;
        // exception parameter
        case EXCEPTION_PARAMETER:
            // TODO: how do we separate which of the types it is on?
            System.out.println("Handle exception parameters!");
            break;
        // method parameter
        case METHOD_PARAMETER:
        case METHOD_PARAMETER_COMPONENT:
            n += 1; // parameter_index
            break;
        // method/constructor type argument
        case NEW_TYPE_ARGUMENT:
        case NEW_TYPE_ARGUMENT_COMPONENT:
        case METHOD_TYPE_ARGUMENT:
        case METHOD_TYPE_ARGUMENT_COMPONENT:
            n += 2; // offset
            n += 1; // type index
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
            throw new AssertionError("Unknown target type: " + pos.type);
        }

        if (pos.type.hasLocation()) {
            n += 2; // length
            n += 1 * pos.location.size(); // actual array size
        }

        return n;
    }

    // Code duplicated from com.sun.tools.javac.code.TypeAnnotationPosition
    public static class Position {

        public TargetType type = TargetType.UNKNOWN;

        // For generic/array types.
        public List<Integer> location = new ArrayList<Integer>();

        // For typecasts, type tests, new (and locals, as start_pc).
        public int offset = -1;

        // For locals.
        public int[] lvarOffset = null;
        public int[] lvarLength = null;
        public int[] lvarIndex = null;

        // For type parameter bound
        public int bound_index = Integer.MIN_VALUE;

        // For type parameter and method parameter
        public int parameter_index = Integer.MIN_VALUE;

        // For class extends, implements, and throws classes
        public int type_index = Integer.MIN_VALUE;

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

            sb.append(']');
            return sb.toString();
        }
    }

    // Code duplicated from com.sun.tools.javac.code.TargetType
    // Except for the IsLocal flag, which can be removed here.
    public enum TargetType {

        // Some target types are commented out, because Java doesn't permit such
        // targets.  They are included here to confirm that their omission is
        // intentional and not accidental.

        // The term "component" is used for type arguments, nested arrays, and
        // outer class types, whichever are legal in the current context.

        /** For annotations on a class type parameter declaration. */
        CLASS_TYPE_PARAMETER(0x00, HasParameter),

        // Invalid location.
        // CLASS_TYPE_PARAMETER_COMPONENT(0x01, HasLocation, HasParameter),

        /** For annotations on a method type parameter declaration. */
        METHOD_TYPE_PARAMETER(0x02, HasParameter),

        // Invalid location.
        // METHOD_TYPE_PARAMETER_COMPONENT(0x03, HasLocation, HasParameter),

        /** For annotations on the type of an "extends" or "implements" clause. */
        CLASS_EXTENDS(0x10),

        /** For annotations on the component of an "extends" or "implements" clause. */
        CLASS_EXTENDS_COMPONENT(0x11, HasLocation),

        /** For annotations on a bound of a type parameter of a class. */
        CLASS_TYPE_PARAMETER_BOUND(0x12, HasBound, HasParameter),

        /**
         * For annotations on a component of a bound of a type
         * parameter of a class.
         */
        CLASS_TYPE_PARAMETER_BOUND_COMPONENT(0x13, HasBound, HasLocation, HasParameter),

        /** For annotations on a bound of a type parameter of a method. */
        METHOD_TYPE_PARAMETER_BOUND(0x14, HasBound, HasParameter),

        /**
         * For annotations on a component of a bound of a type
         * parameter of a method.
         */
        METHOD_TYPE_PARAMETER_BOUND_COMPONENT(0x15, HasBound, HasLocation, HasParameter),

        /** For annotations on a field. */
        FIELD(0x16),

        /** For annotations on a component of a field. */
        FIELD_COMPONENT(0x17, HasLocation),

        /** For annotations on a method return type. */
        METHOD_RETURN(0x18),

        /** For annotations on a component of a method return type. */
        METHOD_RETURN_COMPONENT(0x19, HasLocation),

        /** For annotations on the method receiver. */
        METHOD_RECEIVER(0x1A),

        /** For annotations on a component of the method receiver. */
        METHOD_RECEIVER_COMPONENT(0x1B, HasLocation),

        /** For annotations on a method parameter. */
        METHOD_PARAMETER(0x1C),

        /** For annotations on a component of a method parameter. */
        METHOD_PARAMETER_COMPONENT(0x1D, HasLocation),

        /** For annotations on a throws clause in a method declaration. */
        THROWS(0x1E),

        // Invalid location.
        // THROWS_COMPONENT(0x1F, HasLocation),

        /** For annotations on a local variable. */
        LOCAL_VARIABLE(0x80),

        /** For annotations on a component of a local variable. */
        LOCAL_VARIABLE_COMPONENT(0x81, HasLocation),

        /** For annotations on a resource variable. */
        RESOURCE_VARIABLE(0x82),

        /** For annotations on a component of a resource variable. */
        RESOURCE_VARIABLE_COMPONENT(0x83, HasLocation),

        /** For annotations on an exception parameter. */
        EXCEPTION_PARAMETER(0x84),

        // Invalid location.
        // EXCEPTION_PARAMETER_COMPONENT(0x85, HasLocation),

        /** For annotations on a typecast. */
        TYPECAST(0x86),

        /** For annotations on a component of a typecast. */
        TYPECAST_COMPONENT(0x87, HasLocation),

        /** For annotations on a type test. */
        INSTANCEOF(0x88),

        /** For annotations on a component of a type test. */
        INSTANCEOF_COMPONENT(0x89, HasLocation),

        /** For annotations on an object creation expression. */
        NEW(0x8A),

        /** For annotations on a component of an object creation expression. */
        NEW_COMPONENT(0x8B, HasLocation),

        /** For annotations on a type argument of an object creation expression. */
        NEW_TYPE_ARGUMENT(0x8C),

        /** For annotations on the component of a type argument of an object creation expression. */
        NEW_TYPE_ARGUMENT_COMPONENT(0x8D, HasLocation),

        /** For annotations on a type argument of a method call. */
        METHOD_TYPE_ARGUMENT(0x8E),

        /** For annotations on the component of a type argument of a method call. */
        METHOD_TYPE_ARGUMENT_COMPONENT(0x8F, HasLocation),

        /** For annotations with an unknown target. */
        UNKNOWN(0xFFFF);

        private static final int MAXIMUM_TARGET_TYPE_VALUE = 0x9A;

        private final int targetTypeValue;
        private Set<TargetAttribute> flags;

        TargetType(int targetTypeValue, TargetAttribute... attrs) {
            if (targetTypeValue < Character.MIN_VALUE
                || targetTypeValue > Character.MAX_VALUE)
                throw new AssertionError("Attribute type value needs to be a char: " + targetTypeValue);
            this.targetTypeValue = (char)targetTypeValue;
            this.flags = EnumSet.noneOf(TargetAttribute.class);
            for (TargetAttribute attr : attrs)
                this.flags.add(attr);
        }

        /**
         * Returns whether or not this TargetType represents an annotation whose
         * target is an inner type of a generic or array type.
         *
         * @return true if this TargetType represents an annotation on an inner
         *         type, false otherwise
         */
        public boolean hasLocation() {
            return flags.contains(HasLocation);
        }

        public TargetType getGenericComplement() {
            if (hasLocation())
                return this;
            else
                return fromTargetTypeValue(targetTypeValue() + 1);
        }

        /**
         * Returns whether or not this TargetType represents an annotation whose
         * target has a parameter index.
         *
         * @return true if this TargetType has a parameter index,
         *         false otherwise
         */
        public boolean hasParameter() {
            return flags.contains(HasParameter);
        }

        /**
         * Returns whether or not this TargetType represents an annotation whose
         * target is a type parameter bound.
         *
         * @return true if this TargetType represents an type parameter bound
         *         annotation, false otherwise
         */
        public boolean hasBound() {
            return flags.contains(HasBound);
        }

        public int targetTypeValue() {
            return this.targetTypeValue;
        }

        private static TargetType[] targets = null;

        private static TargetType[] buildTargets() {
            TargetType[] targets = new TargetType[MAXIMUM_TARGET_TYPE_VALUE + 1];
            TargetType[] alltargets = values();
            for (TargetType target : alltargets)
                if (target.targetTypeValue != UNKNOWN.targetTypeValue)
                    targets[target.targetTypeValue] = target;
            for (int i = 0; i <= MAXIMUM_TARGET_TYPE_VALUE; ++i)
                if (targets[i] == null)
                    targets[i] = UNKNOWN;
            return targets;
        }

        public static boolean isValidTargetTypeValue(int tag) {
            if (targets == null)
                targets = buildTargets();

            if (((char)tag) == ((char)UNKNOWN.targetTypeValue))
                return true;

            return (tag >= 0 && tag < targets.length);
        }

        public static TargetType fromTargetTypeValue(int tag) {
            if (targets == null)
                targets = buildTargets();

            if (((char)tag) == ((char)UNKNOWN.targetTypeValue))
                return UNKNOWN;

            if (tag < 0 || tag >= targets.length)
                throw new IllegalArgumentException("Unknown TargetType: " + tag);
            return targets[tag];
        }
    }

    static enum TargetAttribute {
        HasLocation, HasParameter, HasBound;
    }
}
