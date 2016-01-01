package edu.rpi.reimutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import edu.rpi.AnnotatedValue;
import edu.rpi.Constraint;
import edu.rpi.InferenceTransformer;

public class LibraryUtilities {
	
	private HashMap<AnnotatedValue,HashSet<Constraint>> contextToLibraryCallConstraints; 
	private ConstraintGraph cg;
	
	public LibraryUtilities(ConstraintGraph graph) {
		contextToLibraryCallConstraints = new HashMap<AnnotatedValue, HashSet<Constraint>>();
		cg = graph;
	}
	
	
	public void storeLibraryCall(AnnotatedValue context, Constraint c) {
		HashSet<Constraint> callConstraints = contextToLibraryCallConstraints.get(context);
		if (callConstraints == null) {
			callConstraints = new HashSet<Constraint>();
			contextToLibraryCallConstraints.put(context,callConstraints);
		}
		callConstraints.add(c);
	}
	
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
	private void processLibraryCall(Graph<AnnotatedValue, CfgSymbol> graph, HashSet<Constraint> callConstraints) {
		ArrayList<AnnotatedValue> params = new ArrayList<AnnotatedValue>();  // list of parameters at call
		ArrayList<AnnotatedValue> actuals = new ArrayList<AnnotatedValue>(); // list of corresponding actuals
		AnnotatedValue lhs = null; // left-hand-side at call
		
		for (Constraint c : callConstraints) {
			if (c.getLeft() instanceof AnnotatedValue.AdaptValue) {
				// return
				assert !(c.getRight() instanceof AnnotatedValue.AdaptValue);
				lhs = c.getRight();
			}
			else { // params
				assert (c.getRight() instanceof AnnotatedValue.AdaptValue);
				assert !(c.getLeft() instanceof AnnotatedValue.AdaptValue);
				AnnotatedValue.AdaptValue right = (AnnotatedValue.AdaptValue) c.getRight();
				params.add(right.getDeclValue());
				actuals.add(c.getLeft());				
			}
		}
		if (lhs != null) {
			// create actual -> lhs constraints
			for (int i=0; i<params.size(); i++) {
				if (!actuals.get(i).equals(lhs)) {
					// skipEdge should always return false when cg is CallsGraph!!! TODO: add assert here.
					if (!cg.skipAddEdge(actuals.get(i),lhs)) {
						cg.processLocal(actuals.get(i),lhs);
						cg.processFieldClose(actuals.get(i),lhs,ConstraintGraph.LIB,lhs);
						System.out.println("Added LIB EMPTY edge from "+actuals.get(i)+" to "+lhs);
					}
					else {
						cg.processFieldClose(actuals.get(i),lhs,ConstraintGraph.LIB,lhs);
						System.out.println("Added LIB CLOSE edge from "+actuals.get(i)+" to "+lhs);
					}
					// end of TODO fix.
					// System.out.println("Added edge from "+actuals.get(i)+" to "+lhs);
				}
			}
		}
		for (int i=0; i<params.size(); i++) {
			//System.out.println(params.get(i));
			if (cg.isMutable(params.get(i))) {
				System.out.println("Mutable parameter: "+params.get(i));
				for (int j=0; j<actuals.size(); j++) {
					if (!actuals.get(j).equals(actuals.get(i))) {
						// skipEdge should always return false when cg is a CallsGraph!
						if (!cg.skipAddEdge(actuals.get(j),actuals.get(i))) {
							cg.processLocal(actuals.get(j),actuals.get(i));
						}
						else {
							cg.processFieldOpen(actuals.get(j),actuals.get(i),ConstraintGraph.LIB,actuals.get(i));
							System.out.println("Added LIB OPEN edge from "+actuals.get(j)+" to "+actuals.get(i));
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
			for (Constraint c : contextToLibraryCallConstraints.get(key)) {
				SootMethod m = extractLibraryMethod(c);
				if (m.getName().equals("append") || m.getName().equals("toString")) continue;
				if (m.getDeclaringClass().getName().equals("java.lang.String") || 
						m.getDeclaringClass().getName().equals("java.lang.StringBuffer")) continue;
				processLibraryCall(graph, contextToLibraryCallConstraints.get(key));
			}
		}
		System.out.println("There are "+contextToLibraryCallConstraints.keySet().size()+" library call sites.");
		
	}
	
	public boolean isOverriden(SootMethod m) {
		boolean result = false;
		
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
				System.out.println("found overriden method: "+m+" overriden by "+sub);
				return true;
			}
		}		
		return result;
	}
	
	// TODO: Extremely ugly.
	// effects: if library constraint, store in libraryUtils
	// by context value for later processing	
	public boolean isLibraryAdaptConstraint(Constraint c, InferenceTransformer reimTransformer) {
		// We have a constraint x |> ret <: lhs 
		if ((c.getLeft() instanceof AnnotatedValue.AdaptValue) &&
				(((AnnotatedValue.AdaptValue) c.getLeft()).getDeclValue().getEnclosingMethod() != null) &&
				reimTransformer.isLibraryMethod(((AnnotatedValue.AdaptValue) c.getLeft()).getDeclValue().getEnclosingMethod())) {
			AnnotatedValue context = ((AnnotatedValue.AdaptValue) c.getLeft()).getContextValue();
			SootMethod enclMethod = ((AnnotatedValue.AdaptValue) c.getLeft()).getDeclValue().getEnclosingMethod();
			storeLibraryCall(context,c);
			//TODO: DOUBLE CHECK THIS! Have to return false if library method is overriden by user
			if (isOverriden(enclMethod)) { return false; };
			return true;
		}
		// We have x <: y |> param
		else if ((c.getRight() instanceof AnnotatedValue.AdaptValue) && 
				(((AnnotatedValue.AdaptValue) c.getRight()).getDeclValue().getEnclosingMethod() != null) &&
				reimTransformer.isLibraryMethod(((AnnotatedValue.AdaptValue) c.getRight()).getDeclValue().getEnclosingMethod())) {
			AnnotatedValue context = ((AnnotatedValue.AdaptValue) c.getRight()).getContextValue();
			SootMethod enclMethod = ((AnnotatedValue.AdaptValue) c.getRight()).getDeclValue().getEnclosingMethod();
			storeLibraryCall(context,c);
			//TODO: DOUBLE CHECK. Have to return false if library method is overriden by user
			if (isOverriden(enclMethod)) { return false; }
			return true;
		}
		else {
			return false;
		}
		
	}
	
}
