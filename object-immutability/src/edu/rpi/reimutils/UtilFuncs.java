package edu.rpi.reimutils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import checkers.inference.reim.quals.Polyread;
import checkers.inference.reim.quals.Readonly;
import edu.rpi.AnnotatedValue;
import edu.rpi.AnnotationUtils;
import edu.rpi.Constraint;
import edu.rpi.InferenceTransformer;
import soot.ArrayType;
import soot.Hierarchy;
import soot.PrimType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.jimple.toolkits.callgraph.ReachableMethods;

public class UtilFuncs {

	public static boolean isArrayOfSimpleType(AnnotatedValue v) {
		if ((v.getType() instanceof ArrayType) && 
				!(((ArrayType) v.getType()).getElementType() instanceof RefLikeType))
			return true;
		else
			return false;
	}
	
	public static boolean isReadonly(AnnotatedValue v, InferenceTransformer reimTransformer) {
		if (v.getName().startsWith("fake-array-element")) return false; // Another kludge :(. 
		// Need above to avoid looking for annos set, which is NULL
		
		Set<Annotation> annos = v.getAnnotations(reimTransformer);
		//if (v.getId() == 8981) System.out.println("SET of annotations for: "+v.toString()+" is "+annos);
		if (v instanceof AnnotatedValue.AdaptValue) { // if AdaptValue, annos is empty
			AnnotatedValue.AdaptValue av = (AnnotatedValue.AdaptValue) v;  
			if (isReadonly(av.getDeclValue(),reimTransformer)) 
				return true;
			else if (isReadonly(av.getContextValue(),reimTransformer) && 
					av.getDeclValue().getAnnotations(reimTransformer).contains(AnnotationUtils.fromClass(Polyread.class)) ) 
				return true;
			else
				return false;
		}
		
		// If we get here, then v is a regular AnnotatedValue. It can be readonly or an array of simple types byte[] for ex.
		if ( annos.contains(AnnotationUtils.fromClass(Readonly.class)) || 
				isArrayOfSimpleType(v) ) 
			return true;
		else 
			return false;
	}
	
	public static boolean isThisOfNextElement(AnnotatedValue v) {
		if (v.getEnclosingMethod() != null && v.getEnclosingMethod().getName().equals("nextElement")
			&& v.getEnclosingMethod().getDeclaringClass().getName().equals("java.util.Enumeration"))
			return true;
		else 
			return false;
	}
	
	public static boolean isMutable(AnnotatedValue v, InferenceTransformer reimTransformer) {
		assert !(v instanceof AnnotatedValue.AdaptValue);
		Set<Annotation> annos = v.getAnnotations(reimTransformer);
		// System.out.println("Set of annotations for: "+v.toString()+" is "+annos);
		if (annos.contains(AnnotationUtils.fromClass(Readonly.class))) 
			return false;
		else if (annos.contains(AnnotationUtils.fromClass(Polyread.class)))
			return false;
		else
			return true;
	}
	
	// modifies: map
	// effects: adds v to the set for key key; no effect if v already there
	static <T,E> void addToMap(Map<T,Set<E>> map, T key, E v) {
		Set<E> set = map.get(key);
		if (set == null) {
			set = new HashSet<E>();
			map.put(key,set);
		}
		set.add(v);
	}
	
	public static boolean typeCompatible(Type alloc, Type var) {
		// System.out.println("Starting typeCompatible: "+alloc+" and "+var);
		if (alloc.equals(var)) {
			return true;
		}
		else if (var instanceof ArrayType) {
			// then alloc must be an array too
			if (alloc instanceof ArrayType) {
				return typeCompatible(((ArrayType) alloc).getElementType(), ((ArrayType) var).getElementType());
			}
			else {
				return false;
			}
		}
		else if (var instanceof RefType) {
			RefType ref = (RefType) var;
			if (ref.getSootClass().hasSuperclass() == false) {
				// this indicates that var is java.lang.Object
				return true;
			}
			else if (!(alloc instanceof RefType)) {
				// Can't have an array alloc when var is not an array	
				// System.out.println("HERE for "+alloc+" and "+var);
				return false;
			}
			else if (ref.getSootClass().isInterface()) {
				// var is an interface. 
				// TODO: Double check whether transitive interfaces are included into getInterfaces:
				// E.g., X implements I, where I extends J. Would J be into getInterfaces? Assume yes.
				SootClass allocClass = ((RefType) alloc).getSootClass();
				SootClass curr = allocClass;
				HashSet<SootClass> allInterfaces = new HashSet<SootClass>();
				while (curr.hasSuperclass()) {
					allInterfaces.addAll(curr.getInterfaces());
					curr = curr.getSuperclass();
				}				
				boolean result = allInterfaces.contains(ref.getSootClass());
				return result;
			}
			else { 
				// is class 
				return (alloc.merge(ref,Scene.v()).equals(ref));
			}
		}			
		else {
			// Shouldn't happen.
			return false;
		}
	}
	
	
	// debugging utils
	
	public static AnnotatedValue getNodeRepFromId(int id, Map<AnnotatedValue,AnnotatedValue> nodeToRep) {
		for (AnnotatedValue node : nodeToRep.keySet()) {
			if (node.getId() == id) { 
				return nodeToRep.get(node);
			}
		}	
		return null; // SOULDN'T HAPPEN!
	}
	
	public static boolean isOverriden(SootMethod m) {
		
		if (m.getName().equals("<init>")) return false;
 		Hierarchy hier = Scene.v().getActiveHierarchy();
		SootClass c = m.getDeclaringClass();
		List<SootClass> l; 
		if (c.isInterface()) {
			l = hier.getDirectImplementersOf(c);
		}
		else {
			l = hier.getDirectSubclassesOf(c);
		}
		for (SootClass sub : l) {
			if (!sub.isLibraryClass()) {
				if (sub.declaresMethod(m.getSubSignature())) {	
					//System.out.println("found overriden method: "+m+" overriden by "+sub);
					return true;
				}
			}
		}		
		return false;
	}
	
	public static boolean isCallConstraint(Constraint c) {
		if (c.getRight() instanceof AnnotatedValue.MethodAdaptValue)
			return true;
		else
			return false;
	}
	
	public static boolean isReturnConstraint(Constraint c) {
		if (c.getLeft() instanceof AnnotatedValue.MethodAdaptValue)
			return true;
		else
			return false;
	}
	
	// requires is MethodAdaptValue adapt
	public static AnnotatedValue getAdaptedValue(AnnotatedValue adapt) {
		AnnotatedValue.MethodAdaptValue side = (AnnotatedValue.MethodAdaptValue) adapt;
		return side.getDeclValue();
	}
	
	// requires isCallConstraint(callConstraint)
	public static AnnotatedValue getContextValue(AnnotatedValue adapt) {
		AnnotatedValue.MethodAdaptValue side = (AnnotatedValue.MethodAdaptValue) adapt;
		return side.getContextValue();
	}
	
	public static boolean extendsLibraryClass(SootClass c) {
		Hierarchy hier = Scene.v().getActiveHierarchy();
		HashSet<SootClass> allSuperclasses = new HashSet<SootClass>();
		if (c.isInterface()) {
			for (SootClass i : c.getInterfaces()) {
				allSuperclasses.addAll(hier.getSuperinterfacesOfIncluding(i));
			}
		}
		else {
			allSuperclasses.addAll(hier.getSuperclassesOf(c));
			for (SootClass i : c.getInterfaces()) {
				allSuperclasses.addAll(hier.getSuperinterfacesOfIncluding(i));
			}
		}
		for (SootClass sup : allSuperclasses) {
			if (sup.getName().equals("java.lang.Object")) continue;
			if (sup.isLibraryClass()) return true;
		}
		return false;
	}
	
	public static boolean extendsLibraryMethod(SootMethod m) {
				
		if (m.getName().equals("<init>")) return false;
		
		Hierarchy hier = Scene.v().getActiveHierarchy();
		SootClass c = m.getDeclaringClass();
		if (c.isLibraryClass()) return false;
		
		HashSet<SootClass> allSuperclasses = new HashSet<SootClass>();
		if (c.isInterface()) {
			for (SootClass i : c.getInterfaces()) {
				allSuperclasses.addAll(hier.getSuperinterfacesOfIncluding(i));
			}
		}
		else {
			allSuperclasses.addAll(hier.getSuperclassesOf(c));
			for (SootClass i : c.getInterfaces()) {
				allSuperclasses.addAll(hier.getSuperinterfacesOfIncluding(i));
			}
		}
		for (SootClass sup : allSuperclasses) {
			// if (sup.getName().equals("java.lang.Object")) continue;
			if (sup.isLibraryClass() && sup.declaresMethod(m.getSubSignature())) return true;
		}
		return false;
	}
	
	public static Set<SootMethod> retrieveOverridingMethods(SootMethod m, SootClass receiverClass) {
		Set<SootMethod> result = new HashSet<SootMethod>();		
		SootClass c = receiverClass;
		SootClass declClass = m.getDeclaringClass();
		
		
		if (m.getName().equals("run") && 
				m.getDeclaringClass().getName().equals("org.eclipse.core.runtime.adaptor.EclipseStarter")) 
			System.out.println("retrieving callees for "+m+" and receiver class "+receiverClass);
		
		// if staticinvoke or specialinvoke of constructor or private method, return just m.
		if (m.isStatic() || m.getName().equals("<init>") || m.isPrivate()) {
			result.add(m);
			return result;
		}
		// if a call to super
		//if (!receiverClass.equals(declClass) && receiverClass.declaresMethod(m.getSubSignature())) {
	    //	result.add(receiverClass.getMethod(m.getSubSignature()));
		//	return result;
		//}
		
		// Otherwise, resolve a regular virtual call
		if (m.getName().equals("execute")) System.out.println("HERE... resolving... "+m);
		result = resolveVirtual(receiverClass,m.getSubSignature());
		
		if (result.size() == 0) {
			// call to a method implemented in a superclass.
			result.add(m);
		}
		
		return result;
		
		// if call to super TODO: revisit!
		/*
		if (!m.getDeclaringClass().equals(receiverClass) && receiverClass.declaresMethod(m.getSubSignature())) {
			result.add(m);
			return result;
		}
		
		// otherwise, virtualinvoke or interfaceinvoke
		HashSet<SootClass> allSubclasses = new HashSet<SootClass>();
		if (c.isInterface()) {
			//if (m.getName().equals("execute")) System.out.println("HERE for execute! "+);
			allSubclasses.addAll(hier.getImplementersOf(c));
		}
		else {
			// Find the superclass declaring m
			// If Soot&Reim correct, it must terminate TODO: Add checks. UGLY
			while (c.hasSuperclass() && c.declaresMethod(m.getSubSignature()) == false) {
				c = c.getSuperclass();
			}
			if (!c.getName().equals("java.lang.Object"))
				allSubclasses.addAll(hier.getSubclassesOfIncluding(c));
		}
		for (SootClass sub : allSubclasses) {
			if (sub.isLibraryClass()) continue;
			if (sub.declaresMethod(m.getSubSignature())) {
				result.add(sub.getMethod(m.getSubSignature()));
			}
		}
		return result;
		*/
	}
	
	public static Set<SootMethod> resolveVirtual(SootClass receiverClass, String subSignature) {
		Set<SootMethod> result = new HashSet<SootMethod>();
		HashSet<SootClass> allSubclasses = new HashSet<SootClass>();

		// if (subSignature.indexOf(" connect(")>=0) Scene.v().loadDynamicClasses();
			
		Hierarchy hier = Scene.v().getActiveHierarchy();
				
		if (receiverClass.isInterface()) {
			//if (subSignature.indexOf(" run(java.lang.Object")>=0) System.out.println("HERE for execute! "+receiverClass+" and "+subSignature);
			HashSet<SootClass> implementers = new HashSet<SootClass>();
			implementers.addAll(hier.getImplementersOf(receiverClass));
			for (SootClass i : implementers) {
				//if (subSignature.indexOf("loadClass")>=0) System.out.println("=====ADDED IMPLEMENTER: "+i);
				allSubclasses.addAll(hier.getSubclassesOfIncluding(i));
			}
		}
		else {
			//if (subSignature.indexOf(" run(java.lang.Object)")>=0) System.out.println("======== HERE... is class..."+receiverClass+" # subclasses "+hier.getSubclassesOfIncluding(receiverClass).size());
			allSubclasses.addAll(hier.getSubclassesOfIncluding(receiverClass));
		}
		for (SootClass sub : allSubclasses) {
			//if (subSignature.indexOf("(")>=0) System.out.println("=====TRYING FOR SUBCLASS: "+sub +" and "+subSignature);
			if (sub.isLibraryClass()) continue;			
			if (sub.declaresMethod(subSignature)) {
				//if (subSignature.indexOf("execute(")>=0) System.out.println("=====DECLARES METHOD : "+sub.getMethod(subSignature)+" for "+sub);
				result.add(sub.getMethod(subSignature));
			}
		}
		return result;
	}
	
	public static boolean isNoReturnNoParamStatic(SootMethod m) {
		if (!m.isStatic())
			return false;
		else {
			if (m.getParameterCount() == 0 && (m.getReturnType() == VoidType.v() || m.getReturnType() instanceof PrimType)) {
				return true;
			}
			else {				
				for (int i=0; i<m.getParameterCount(); i++) {
					if (!(m.getParameterType(i) instanceof PrimType)) 
						return false;
				}				
				return true;
			}
		}
	}
	/* No call graph in -app mode. It's a bummer...
	public static boolean isReachable(SootMethod m) {
		ReachableMethods reach = Scene.v().getReachableMethods();
		if (reach == null) {
			System.out.println("NO REACH METHODS. Its a BUMMER...");
			return true;
		}
		return reach.contains(m);
	}
	*/
}
