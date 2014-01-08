package edu.rpi;

import java.lang.annotation.*;
import java.util.*;

import soot.tagkit.*;

import checkers.quals.SubtypeOf;

public class AnnotationUtils {

    private static AnnotationUtils instance;

//    public static AnnotationUtils v() {
//        if (instance == null)
//            instance = new AnnotationUtils();
//        return instance;
//    }


    /** Caching for annotation creation. */
    private static final Map<String, Annotation> annotationsFromNames
        = new HashMap<String, Annotation>();

    private static final Map<String, Set<String>> supertypes 
        = new HashMap<String, Set<String>>();

    public static Annotation fromName(String name) {
        if (annotationsFromNames.containsKey(name))
            return annotationsFromNames.get(name);

        Annotation ret = null;
        try {
            final Class c = Class.forName(name); 
            ret = new Annotation() {
                String toString = "@" + c.getSimpleName();
                @Override
                public Class<? extends Annotation> annotationType() {
                    return c;
                }
                @Override 
                public String toString() {
                    return toString;
                }
                @Override
                public boolean equals(Object obj) {
                    return (obj instanceof Annotation) 
                        && annotationType().equals(((Annotation) obj).annotationType());
                }
            };
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: Cannot find class: " + name);
        }

        return ret;
    }

    public static Annotation fromAnnotationTag(AnnotationTag aTag) {
        String type = aTag.getType().substring(1);
        type = type.substring(0, type.length() - 1);
        type = type.replace('/', '.');
//        if (type.endsWith("This"))
//            type.substring(0, type.length() - 4);
        return fromName(type);
    }

    public static Annotation fromClass(Class<? extends Annotation> clazz) {
        return fromName(clazz.getCanonicalName());
    }


    public static Set<Annotation> createAnnotationSet() {
        return new TreeSet<Annotation>(annotationOrdering());
    }

    public static boolean isSubtype(Annotation sub, Annotation sup) {
        String subStr = sub.annotationType().getCanonicalName();
        String supStr = sup.annotationType().getCanonicalName();
        Set<String> sups = supertypes.get(sub); 
        if (sups == null) {
            sups = new HashSet<String>();
            // add itself
            sups.add(subStr);
            Class<? extends Annotation> clazz = sub.annotationType();
            Class<? extends Annotation>[] superQualifiers 
                = clazz.getAnnotation(SubtypeOf.class).value();
            for (Class<? extends Annotation> c : superQualifiers) {
                sups.add(c.getCanonicalName());
            }
            supertypes.put(subStr, sups);
        }
        return sups.contains(supStr);
    }

    public static Comparator<Annotation> annotationOrdering() {
        return ANNOTATION_ORDERING;
    }

    private static final Comparator<Annotation> ANNOTATION_ORDERING
        = new Comparator<Annotation>() {
        @Override
        public int compare(Annotation a1, Annotation a2) {
            if (a1 == null || a2 == null) {
                if (a1 == a2)
                    return 0;
                else if (a1 == null)
                    return -1;
                else if (a2 == null)
                    return 1;
            }

            String n1 = a1.toString();
            String n2 = a2.toString();

            return n1.compareTo(n2);
        }
    };
}
