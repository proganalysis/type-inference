package edu.rpi;

import soot.SootClass;
import soot.SootMethod;
import java.util.*;

public class InferenceUtils {

    public static Set<SootClass> getSuperTypes(SootClass sc) {
        Set<SootClass> supertypes = new LinkedHashSet<SootClass>();

        Deque<SootClass> stack = new ArrayDeque<SootClass>();
        stack.push(sc);

        while (!stack.isEmpty()) {
            SootClass current = stack.pop();

            if (current.getName().equals("java.lang.Object"))
                continue;

            SootClass c = current.hasSuperclass() ? current.getSuperclass() : null;
            if (c!= null && !supertypes.contains(c)) {
                stack.push(c);
                supertypes.add(c);
            }
            for (SootClass supertype : current.getInterfaces()) {
                if (!supertypes.contains(supertype)) {
                    stack.push(supertype);
                    supertypes.add(supertype);
                }
            }
        }
        return supertypes;
    }

    public static Map<SootClass, SootMethod> overriddenMethods(SootMethod sm) {
        Map<SootClass, SootMethod> overrides = new HashMap<SootClass, SootMethod>();
        SootClass sc = sm.getDeclaringClass();
        Set<SootClass> supertypes = getSuperTypes(sc); 
        String subSignature = sm.getSubSignature();
        for (SootClass supertype : supertypes) {
            for (SootMethod superMethod : supertype.getMethods()) {
                if (superMethod.getSubSignature().equals(subSignature)
                        && superMethod.getModifiers() == sm.getModifiers()) {
                    overrides.put(supertype, superMethod);
                    break;
                }
            }
        }
        return overrides;
    }
}
