package edu.rpi.reimutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import edu.rpi.AnnotatedValue;
import edu.rpi.Constraint;
import edu.rpi.InferenceTransformer;

public class Libraries {
	
	private Map<AnnotatedValue,Set<Constraint>> contextToLibraryCallConstraints; 
	private ConstraintGraph cg;
	
	public Libraries(ConstraintGraph graph) {
		contextToLibraryCallConstraints = new HashMap<AnnotatedValue, Set<Constraint>>();
		cg = graph;
	}
	
	/*
	public void storeLibraryCall(AnnotatedValue context, Constraint c) {
		HashSet<Constraint> callConstraints = contextToLibraryCallConstraints.get(context);
		if (callConstraints == null) {
			callConstraints = new HashSet<Constraint>();
			contextToLibraryCallConstraints.put(context,callConstraints);
		}
		callConstraints.add(c);
	}
	*/
	
	// requires: either left or right is an AdaptValue
	private SootMethod extractLibraryMethod(Constraint c) {
		AnnotatedValue.AdaptValue value;
		if (c.getLeft() instanceof AnnotatedValue.AdaptValue) {
			value = (AnnotatedValue.AdaptValue) c.getLeft();
		}
		else {
			value = (AnnotatedValue.AdaptValue) c.getRight();
		}
		return value.getDeclValue().getEnclosingMethod();
	}
	
	
	// modifies: graph
	// effects: adds summary edges due to library call
	private void processLibraryCall(Graph<AnnotatedValue, CfgSymbol> graph, Set<Constraint> callConstraints) {
		ArrayList<AnnotatedValue> params = new ArrayList<AnnotatedValue>();  // list of parameters at call
		ArrayList<AnnotatedValue> actuals = new ArrayList<AnnotatedValue>(); // list of corresponding actuals
		AnnotatedValue lhs = null; // left-hand-side at call
		
		for (Constraint c : callConstraints) {
						
			if (UtilFuncs.isReturnConstraint(c)) {
				// return
				assert !(c.getRight() instanceof AnnotatedValue.AdaptValue);
				lhs = c.getRight();
			}
			else { // params
				assert UtilFuncs.isCallConstraint(c);
				assert (c.getRight() instanceof AnnotatedValue.AdaptValue);
				assert !(c.getLeft() instanceof AnnotatedValue.AdaptValue);
				AnnotatedValue.AdaptValue right = (AnnotatedValue.AdaptValue) c.getRight();
				params.add(right.getDeclValue());
				actuals.add(c.getLeft());				
			}
		}
		if (lhs != null && !UtilFuncs.isArrayOfSimpleType(lhs)) {
			// create actual -> lhs constraints
			for (int i=0; i<params.size(); i++) {
				if (!actuals.get(i).equals(lhs)) {
					// skipEdge should always return false when cg is CallsGraph!!! TODO: add assert here.
					if (!cg.skipAddEdge(actuals.get(i),lhs)) {
						cg.processLocal(actuals.get(i),lhs);
						cg.processFieldClose(actuals.get(i),lhs,ConstraintGraph.LIB,lhs);
						//if (params.get(i).toString().indexOf("add(")>=0) System.out.println("Added LIB EMPTY/CLOSE edge from "+actuals.get(i)+" to "+lhs);
					}
					else {
						cg.processFieldClose(actuals.get(i),lhs,ConstraintGraph.LIB,lhs);
						//if (params.get(i).toString().indexOf("add(")>=0) System.out.println("Added LIB CLOSE edge from "+actuals.get(i)+" to "+lhs);
					}
					// end of TODO fix.
					// System.out.println("Added edge from "+actuals.get(i)+" to "+lhs);
				}
			}
		}
		for (int i=0; i<params.size(); i++) {
			//System.out.println(params.get(i));
			// TODO: Improve!
			// If static method, create constraints x_i -> x_j for each i and mutable j.
			// If instance, only create constraints x_i -> x_0 (this)
			if (!params.get(i).getEnclosingMethod().isStatic() && params.get(i).getKind() != AnnotatedValue.Kind.THIS) continue; 
			if (UtilFuncs.isMutable(params.get(i),cg.reimTransformer) && !UtilFuncs.isArrayOfSimpleType(actuals.get(i))) {
				//System.out.println("Mutable parameter: "+params.get(i));
				for (int j=0; j<actuals.size(); j++) {
					if (!actuals.get(j).equals(actuals.get(i))) {
						// skipEdge should always return false when cg is a CallsGraph!
						if (!cg.skipAddEdge(actuals.get(j),actuals.get(i))) {
							cg.processLocal(actuals.get(j),actuals.get(i));
							cg.processFieldOpen(actuals.get(j),actuals.get(i),ConstraintGraph.LIB,actuals.get(i)); 
							//if (params.get(i).toString().indexOf("add(")>=0) System.out.println("Added LIB OPEN/EMPTY edge from "+actuals.get(j)+" to "+actuals.get(i));
						}
						else {
							cg.processFieldOpen(actuals.get(j),actuals.get(i),ConstraintGraph.LIB,actuals.get(i));							
							//if (params.get(i).toString().indexOf("add(")>=0) System.out.println("Added LIB OPEN edge from "+actuals.get(j)+" to "+actuals.get(i));
						}
						/*
						if (actuals.get(j).getType().equals(actuals.get(i).getType())) {
							graph.addEdge(actuals.get(j),actuals.get(i),CfgSymbol.LOCAL);
						}
						else {
							graph.addEdge(actuals.get(j),actuals.get(i),CfgSymbol.getAtomicOpen(ConstraintGraph.LIB));
							System.out.println("Added LIB edge from "+actuals.get(j)+" to "+actuals.get(i));
						}
						*/
						//System.out.println("Added edge from "+actuals.get(j)+" to "+actuals.get(i));
					}
				}
			}
		}

		
	}
	
	public void processLibraryCalls(Graph<AnnotatedValue, CfgSymbol> graph) {
		// TODO Auto-generated method stub
		int calls=0;
		for (AnnotatedValue key : contextToLibraryCallConstraints.keySet()) {
			//System.out.println("Key: "+key);
			SootMethod m = null;
			for (Constraint c : contextToLibraryCallConstraints.get(key)) {
				m = extractLibraryMethod(c);
				break;
			}
				//if (m.getName().equals("append") || m.getName().equals("toString")) continue;
				//if (m.getDeclaringClass().getName().equals("java.lang.String") || 
			if	(m.getDeclaringClass().getName().equals("java.lang.StringBuffer")) continue;
			processLibraryCallWrapper(graph, contextToLibraryCallConstraints.get(key), m);
			
		}
		//System.out.println("There are "+contextToLibraryCallConstraints.keySet().size()+" library call sites.");
		
	}
	
	// TODO: Extremely ugly.
	// effects: if library constraint, store in libraryUtils
	// by context value for later processing	
	public boolean isLibraryAdaptConstraint(Constraint c, InferenceTransformer reimTransformer) {
		// We have a constraint x |> ret <: lhs 
		boolean result;
		if ((c.getLeft() instanceof AnnotatedValue.AdaptValue) &&
				(((AnnotatedValue.AdaptValue) c.getLeft()).getDeclValue().getEnclosingMethod() != null) &&
				reimTransformer.isLibraryMethod(((AnnotatedValue.AdaptValue) c.getLeft()).getDeclValue().getEnclosingMethod())) {
			AnnotatedValue context = ((AnnotatedValue.AdaptValue) c.getLeft()).getContextValue();
			SootMethod enclMethod = ((AnnotatedValue.AdaptValue) c.getLeft()).getDeclValue().getEnclosingMethod();
			UtilFuncs.addToMap(contextToLibraryCallConstraints,context,c);
			//TODO: DOUBLE CHECK THIS! Have to return false if library method is overriden by user
			if (UtilFuncs.isOverriden(enclMethod)) { return false; };
			result = true;
		}
		// We have x <: y |> param
		else if ((c.getRight() instanceof AnnotatedValue.AdaptValue) && 
				(((AnnotatedValue.AdaptValue) c.getRight()).getDeclValue().getEnclosingMethod() != null) &&
				reimTransformer.isLibraryMethod(((AnnotatedValue.AdaptValue) c.getRight()).getDeclValue().getEnclosingMethod())) {
			AnnotatedValue context = ((AnnotatedValue.AdaptValue) c.getRight()).getContextValue();
			SootMethod enclMethod = ((AnnotatedValue.AdaptValue) c.getRight()).getDeclValue().getEnclosingMethod();
			UtilFuncs.addToMap(contextToLibraryCallConstraints,context,c);
			//TODO: DOUBLE CHECK. Have to return false if library method is overriden by user
			if (UtilFuncs.isOverriden(enclMethod)) { return false; }
			result = true;
		}
		else {
			result = false;
		}
		return result;
	}
	
	public void resetContextMap() {
		contextToLibraryCallConstraints = new HashMap<AnnotatedValue, Set<Constraint>>();
	}
	
	private void processLibraryCallWrapper(Graph<AnnotatedValue, CfgSymbol> graph, Set<Constraint> callConstraints, SootMethod m) {
		if (m.getName().equals("toArray") || m.getName().equals("copyInto")) {
			processToArrayCall(graph,callConstraints,m);
		}
		else { // process constraints by default
			processLibraryCall(graph,callConstraints);
		}
	}
	
	// requires: extractMethod of callConstraints is a toArray method!
	private void processToArrayCall(Graph<AnnotatedValue, CfgSymbol> graph, Set<Constraint> callConstraints, SootMethod m) {
		//System.out.println("\n Constraints for method: "+m);
		ArrayList<AnnotatedValue> args = new ArrayList<AnnotatedValue>();
		AnnotatedValue receiver = null;
		AnnotatedValue lhs = null;
		for (Constraint c : callConstraints) {
			if (UtilFuncs.isReturnConstraint(c)) {
				lhs = c.getRight();
			}
			else {
				assert UtilFuncs.isCallConstraint(c);
				if (UtilFuncs.getAdaptedValue(c.getRight()).getKind() == AnnotatedValue.Kind.THIS) {
					receiver = c.getLeft();
				}
				else {
					args.add(c.getLeft());
				}
			}
			//System.out.println("TO ARRAY CONSTRAINTS: "+c);
		}
		assert receiver != null;
		
		// WARNING! This may not be safe as the AnnotatedValue is NOT saved into anno maps of transformer!
		
		
		if (lhs != null) {
			String identifier = m.getSignature()+"@fake-array-element-lhs";
			Type type = Scene.v().getObjectType();
			AnnotatedValue fakeArrayElement = new AnnotatedValue(identifier, type, AnnotatedValue.Kind.LOCAL, null); 
			cg.processFieldClose(receiver,fakeArrayElement,ConstraintGraph.LIB,fakeArrayElement);
			cg.processFieldOpen(fakeArrayElement,lhs,ConstraintGraph.ARRAY,lhs);
		}
		for (AnnotatedValue arg : args) {
			String identifier = m.getSignature()+"@fake-array-element-arg";
			Type type = Scene.v().getObjectType();
			AnnotatedValue fakeArrayElement = new AnnotatedValue(identifier, type, AnnotatedValue.Kind.LOCAL, null); 
			cg.processFieldClose(receiver,fakeArrayElement,ConstraintGraph.LIB,fakeArrayElement);
			cg.processFieldOpen(fakeArrayElement,arg,ConstraintGraph.ARRAY,arg);
			if (lhs != null) {
				cg.processLocal(arg,lhs);
			}
		}
	}
	
	
	// TODO: process lhs = doPriviliged(p) becomes lhs = p.run();
	
}
