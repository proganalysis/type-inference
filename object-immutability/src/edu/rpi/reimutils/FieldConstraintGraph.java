package edu.rpi.reimutils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import soot.ArrayType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import edu.rpi.AnnotatedValue;
import edu.rpi.InferenceTransformer;

public class FieldConstraintGraph extends ConstraintGraph {

	public FieldConstraintGraph(InferenceTransformer transformer) {
		super(transformer);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addFieldOpen(AnnotatedValue left, AnnotatedValue right,
			AnnotatedValue annotation) {
		graph.addEdge(left,right,CfgSymbol.getAtomicOpen(annotation));
		graph.addEdge(left,right,CfgSymbol.OPENPAREN);
	}

	@Override
	protected void addFieldClose(AnnotatedValue left, AnnotatedValue right,
			AnnotatedValue annotation) {
		graph.addEdge(left,right,CfgSymbol.getAtomicClose(annotation));
		graph.addEdge(left,right,CfgSymbol.CLOSEPAREN);
	}

	@Override
	protected void addCallOpen(AnnotatedValue left, AnnotatedValue right,
			AnnotatedValue annotation) {
		if (!left.equals(right)) graph.addEdge(left,right,CfgSymbol.LOCAL);
	}

	@Override
	protected void addCallClose(AnnotatedValue left, AnnotatedValue right,
			AnnotatedValue annotation) {
		if (!left.equals(right)) graph.addEdge(left,right,CfgSymbol.LOCAL);
	}

	@Override
	protected void addLocal(AnnotatedValue left, AnnotatedValue right) {
		if (!left.equals(right)) graph.addEdge(left,right,CfgSymbol.LOCAL);
	}

	@Override
	protected void printHeaderString() {
		System.out.println("Printing fields graph");
	}
	
	
	protected void dynamicClosure(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Set<Edge<AnnotatedValue, CfgSymbol>> visitedEdges) {
		int count=0;
		while (!queue.isEmpty()) {
			System.out.println("queue.size: "+queue.size());
			Edge<AnnotatedValue,CfgSymbol> curr = queue.remove();
			//System.out.println("Current popped edge: "+curr);
			// Invariant: curr is LOCAL
			for (Edge<AnnotatedValue,CfgSymbol> next : graph.getEdgesFrom(curr.getTarget())) {
				addTransitiveLocalEdge(curr,next,queue,visitedEdges);
			}
			/*
			for (Edge<AnnotatedValue,CfgSymbol> prev : graph.getEdgesInto(curr.getSource())) {
				addTransitiveLocalEdge(prev,curr,queue,visitedEdges);
			}
			*/
						
			for (Edge<AnnotatedValue,CfgSymbol> next : graph.getEdgesFrom(curr.getTarget())) {
				for (Edge<AnnotatedValue,CfgSymbol> prev : graph.getEdgesInto(curr.getSource())) {
					if (prev.getLabel().match(next.getLabel())) {
						if (prev.getSource().equals(next.getTarget())) continue;
						addTransitiveLocal(queue,visitedEdges,prev.getSource(),next.getTarget());
						//System.out.println("Added ci LOCAL ci EDGE from: "+prev+" and "+next);
						count++;
						//addLocalEdge(queue,visitedEdges,prev.getSource(),next.getTarget(),CfgSymbol.LOCAL);
					}
				}
			}
		}
		System.out.println("Counted "+count+" first level ci ci edges.");
	}
	
	private boolean hasAtomicOpenPredecessor(AnnotatedValue source) {
		for (Edge<AnnotatedValue,CfgSymbol> prevprev : graph.getEdgesInto(source)) {
			if (prevprev.getLabel() instanceof AtomicOpenParen) {
				return true;
			}
		}
		return false;
	}
	
	private void addTransitiveLocal(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Set<Edge<AnnotatedValue, CfgSymbol>> visitedEdges,
			AnnotatedValue left, AnnotatedValue right) {
		if (graph.hasEdge(left,right,CfgSymbol.LOCAL)) return; // edge is already in the graph.
		for (Edge<AnnotatedValue,CfgSymbol> prev : graph.getEdgesInto(left)) {
			if (prev.getLabel() instanceof AtomicOpenParen) {
				addLocalEdge(queue,visitedEdges,left,right,CfgSymbol.LOCAL);
			}
			else if (prev.getLabel() == CfgSymbol.LOCAL) {
				if (hasAtomicOpenPredecessor(prev.getSource())) {
					addLocalEdge(queue,visitedEdges,prev.getSource(),right,CfgSymbol.LOCAL);
				}
			}
		}
		if (!skipAddEdge(left,right)) 
			graph.addEdge(left,right,CfgSymbol.LOCAL);
	}
	
	// TODO: have to implement structural "merge" for ArrayTypes
	// TODO: THIS HAS TO BE REDONE. THIS IS JUST A QUICK THING TO SEE IF IT SCALES!!!
	@Override
	// returns true if left and right are of compatible types
	protected boolean skipAddEdge(AnnotatedValue left, AnnotatedValue right) {
		Type leftType = left.getType();
		Type rightType = right.getType();
		return (!isSubtype(leftType,rightType));
			
	}

	private boolean isSubtype(Type type1, Type type2) {
		if (isObjectType(type1) || isObjectType(type2)) {
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
		
			// TODO: getInterfaces returns _directly_ implemented interfaces only;
			// potentially unsafe.
			/*
			if (leftClass.isInterface()) {
				if (rightClass.getInterfaces().contains(leftClass))
					return true;
				else
					return false;
			}
			else if (rightClass.isInterface()) {
				if (leftClass.getInterfaces().contains(rightClass))
					return true;
				else
					return false;
			} 
			*/
			
			if (leftClass.isInterface() || rightClass.isInterface()) return true;
			
			Type commonSupertype = type1.merge(type2,Scene.v());
			//System.out.println("merge("+leftType+","+rightType+")="+commonSupertype);
			if (commonSupertype.equals(type1) || commonSupertype.equals(type2))
				return true;
			else
				return false;
		}
		else {
			return false;
		}
	}
	
	private boolean isObjectType(Type t) {
		if ((t instanceof RefType) && !(((RefType) t).getSootClass().hasSuperclass()))
			return true;
		else
			return false;
	}

		
	@Override
	protected void addTransitiveEdgeToQueue(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Edge<AnnotatedValue, CfgSymbol> e1, Edge<AnnotatedValue, CfgSymbol> e2) {		
		if (e2.getLabel() == CfgSymbol.LOCAL) {
			Edge<AnnotatedValue, CfgSymbol> newEdge = 
					new Edge<AnnotatedValue,CfgSymbol>(e1.getSource(),e2.getTarget(),CfgSymbol.LOCAL);
			if (!ptGraph.hasEdge(e1.getSource(),e2.getTarget(),CfgSymbol.LOCAL)) {
				ptGraph.addEdge(e1.getSource(),e2.getTarget(),CfgSymbol.LOCAL);
				queue.add(newEdge);
			}
		}
	}

	@Override
	protected void addEdgeToQueue(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Edge<AnnotatedValue, CfgSymbol> e) {
		if (e.getLabel() == CfgSymbol.LOCAL) {
			ptGraph.addEdge(e.getSource(),e.getTarget(),e.getLabel());
			queue.add(new Edge(e.getSource(),e.getTarget(),e.getLabel()));
		}
	}
	
}
