package edu.rpi.reimutils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import soot.ArrayType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import edu.rpi.AnnotatedValue;

public class SCCUtilities {

	// Collapses cg into Strongly Connected Components.
	// effects: collapses cg.graph into a new cg.graph where nodes are SCCs of old cg.graph
	public static void collapseGraph(ConstraintGraph cg, Filter filter) {
		System.out.println("Starting collapse graph.");
		HashMap<AnnotatedValue,HashSet<AnnotatedValue>> equiv = new HashMap();
		
		// Initialize equiv set to node -> {node}
		for (AnnotatedValue node : cg.graph.getNodes()) {
			//System.out.println("Initializing set for NODE: "+node);
			initializeEquivClass(equiv, node);
		}
		
		// Merge equivalence classes
		// Do not merge Array types with non-array types
		for (AnnotatedValue node : cg.graph.getNodes()) {
			List<Edge<AnnotatedValue,CfgSymbol>> edges = cg.graph.getEdgesFrom(node);
			for (Edge<AnnotatedValue,CfgSymbol> edge : edges) {
				if (edge.getLabel() == CfgSymbol.LOCAL && 
						cg.graph.hasEdge(edge.getTarget(),edge.getSource(),CfgSymbol.LOCAL)) {					
					if (filter.skipMerge(edge)) continue; 
					//System.out.println("Merging sets for edge "+edge);
					mergeEquivClasses(equiv, edge);
				}
			}
		}
		// Postcondition: each equiv. class contains either Arrays or non-array types
		
		// returns: reps is the mapping from original variable to its representative
		// effects: nodeToRep adjusts to current variable -> representative
		HashMap<AnnotatedValue, AnnotatedValue> reps = computeRepresentatives(cg, equiv, filter);
		
		adjustGraphAfterCollapse(cg, reps);
								
		
	}

	private static HashMap<AnnotatedValue, AnnotatedValue> computeRepresentatives(
			ConstraintGraph cg,
			HashMap<AnnotatedValue, HashSet<AnnotatedValue>> equiv,
			Filter filter) {
		
		// Compute representative of equivalence class		
		HashMap<AnnotatedValue,AnnotatedValue> reps = new HashMap<AnnotatedValue,AnnotatedValue>();
		HashSet<HashSet<AnnotatedValue>> values = new HashSet<HashSet<AnnotatedValue>>();
		for (HashSet<AnnotatedValue> s : equiv.values()) {
			values.add(s);			
		}
		for (HashSet<AnnotatedValue> s : values) {
			AnnotatedValue rep = filter.getRep(s); 
			
			// Adjusts reps and nodeToRep
			for (AnnotatedValue v : s) {
				reps.put(v,rep);
				cg.nodeToRep.put(v,rep);
			}						
		}
		
		// Adjust nodeToRep
		for (AnnotatedValue node : cg.nodeToRep.keySet()) {
			AnnotatedValue rep = reps.get(node);
			if (rep != null) {
				cg.nodeToRep.put(node,rep);
			}
			else if (reps.get(cg.nodeToRep.get(node)) != null) {				
				cg.nodeToRep.put(node,reps.get(cg.nodeToRep.get(node)));
			}
			// else, leave it as it is, original nodeToRep value.
		}
		
		System.out.println("keys.size(): "+equiv.keySet().size()+" and values.size() "+values.size());
		/*			
		System.out.println("NODE TO REP MAP:");
		for (AnnotatedValue node : cg.nodeToRep.keySet()) {
			System.out.println(node+" maps to "+cg.nodeToRep.get(node));
		}
		*/
		return reps;
	}

	private static void initializeEquivClass(
			HashMap<AnnotatedValue, HashSet<AnnotatedValue>> equiv,
			AnnotatedValue node) {
		HashSet<AnnotatedValue> hs = new HashSet<AnnotatedValue>();
		hs.add(node);
		equiv.put(node,hs);
	}

	private static void mergeEquivClasses(HashMap<AnnotatedValue, HashSet<AnnotatedValue>> equiv, Edge<AnnotatedValue, CfgSymbol> edge) {
		HashSet<AnnotatedValue> hs1 = equiv.get(edge.getSource());
		HashSet<AnnotatedValue> hs2 = equiv.get(edge.getTarget());
		HashSet<AnnotatedValue> merged;
		HashSet<AnnotatedValue> discarded;
		if (hs1.size() < hs2.size()) {
			merged = hs2;
			discarded = hs1;
		}
		else {
			merged = hs1;
			discarded = hs2;
		}
		//for (AnnotatedValue a : merged) System.out.println("--merged set: "+a);
		//for (AnnotatedValue a : discarded) System.out.println("--discarded set: "+a);
		merged.addAll(discarded);
		//for (AnnotatedValue a : merged) System.out.println("--merged set: "+a);
		for (AnnotatedValue n : discarded) {
			equiv.put(n,merged);			
		}
	}
	
	private static void adjustGraphAfterCollapse(ConstraintGraph cg,
			HashMap<AnnotatedValue, AnnotatedValue> reps) {
		Graph<AnnotatedValue,CfgSymbol> theNewGraph = new Graph<AnnotatedValue,CfgSymbol>();
		for (AnnotatedValue node : cg.graph.getNodes()) {
				
			List<Edge<AnnotatedValue,CfgSymbol>> edges = cg.graph.getEdgesFrom(node);
			for (Edge<AnnotatedValue,CfgSymbol> edge : edges) {
				AnnotatedValue repSource = reps.get(edge.getSource());
				AnnotatedValue repTarget = reps.get(edge.getTarget());
				CfgSymbol label = edge.getLabel();
				if (repSource == repTarget && label == CfgSymbol.LOCAL) continue;
				theNewGraph.addEdge(repSource,repTarget,label);
			}
		}
		cg.graph = theNewGraph;
	}

	
	// Helper functions.
	
	private static boolean isArrayType(Type t) {
		if (t instanceof ArrayType) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private static boolean isInterface(Type t) {
		if (t instanceof RefType && ((RefType) t).getSootClass().isInterface()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private static boolean isObject(Type t) {
		if (t instanceof RefType && !((RefType) t).getSootClass().hasSuperclass()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// returns: true if type1 is a subtype of type2
	//          if type2 is Object or type2 is an Interface, then result is true
	private static boolean isSubtype(Type type1, Type type2) {
		// TODO: Fix this. Very ugly, needs refactoring.
		if (isInterface(type2) || isObject(type2)) {
			return true;
		}
		else if (type1.equals(type2)) {
			return true;
		}
		else if ((type1 instanceof ArrayType) && (type2 instanceof ArrayType)) {
			ArrayType arr1 = (ArrayType) type1;
			ArrayType arr2 = (ArrayType) type2;
			return isSubtype(arr1.getElementType(),arr2.getElementType());
		}
		else if ((type1 instanceof RefType) && (type2 instanceof RefType)) {
			RefType leftRef = (RefType) type1;
			SootClass leftClass = leftRef.getSootClass();
			RefType rightRef = (RefType) type2;
			SootClass rightClass = rightRef.getSootClass();
								
			Type commonSupertype = type1.merge(type2,Scene.v());
			//System.out.println("merge("+leftType+","+rightType+")="+commonSupertype);
			if (commonSupertype.equals(type2))
				return true;
			else
				return false;
		}
		else {
			return false;
		}
	}
	
	static boolean isStaticField(AnnotatedValue v) {
		if (v.getEnclosingMethod() == null) {
			return true;
		}
		else {
			if (v.getEnclosingMethod().getName().equals("<clinit>"))
				return true;
			else
				return false;
		}
	}

	
public static abstract class Filter {
	abstract boolean skipMerge(Edge<AnnotatedValue,CfgSymbol> e);
	abstract AnnotatedValue getRep(HashSet<AnnotatedValue> s);	
}

public static class CallFilter extends Filter {

	@Override
	boolean skipMerge(Edge<AnnotatedValue, CfgSymbol> e) {
		// TODO!
		// return isStaticField(source) != isStaticField(target) 
		//System.out.println("IN SKIP MERGE: "+e.getSource() + " === > "+ e.getTarget());
		if (isStaticField(e.getSource()) != isStaticField(e.getTarget())) {
			//System.out.println("Skipping: "+e.getSource() + " === > "+ e.getTarget());
			return true;
		}
		//System.out.println("Not skipping: "+e.getSource() + " === > "+ e.getTarget());
		return false;
	}

	@Override
	AnnotatedValue getRep(HashSet<AnnotatedValue> s) {
		return s.iterator().next();
	}	
}

public static class FieldFilter extends Filter {

	@Override
	boolean skipMerge(Edge<AnnotatedValue, CfgSymbol> edge) {
		return isArrayType(edge.getTarget().getType()) != isArrayType(edge.getSource().getType());
	}

	@Override
	AnnotatedValue getRep(HashSet<AnnotatedValue> s) {
		AnnotatedValue rep = s.iterator().next();
		
		for (AnnotatedValue v : s) {
			if (isSubtype(rep.getType(),v.getType())) {
				rep = v;
			}
		}
		// Postcondition: isSubtype(x,rep) is true for each x in s.
		
		// Check postcondition
		for (AnnotatedValue v : s) {
			assert (isSubtype(v.getType(),rep.getType())) : "Should not happen: "+v.getType() +" IS NOT A SUBTYPE OF rep type "+rep.getType();
		}
		return rep;
	}
	
}

	/*
	private static HashSet<SootClass> getAllInterfaces(SootClass c) {
		HashSet<SootClass> result = new HashSet<SootClass>();
		SootClass curr = c;
		while (curr.hasSuperclass()) {
			result.addAll(curr.getInterfaces());
			curr = curr.getSuperclass();
		}
		HashSet<SootClass> interfaces = new HashSet<SootClass>();
		Queue<SootClass> queue = new LinkedList<SootClass>();
		queue.addAll(result);
		while (!queue.isEmpty()) {
			SootClass inter = queue.remove();
			if (result.contains(inter)) continue;
			result.add(inter);			
			queue.addAll(inter.getInterfaces());
		}
		return result;
	}
	*/
	
}
