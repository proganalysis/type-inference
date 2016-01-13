package edu.rpi.reimutils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
			AnnotatedValue annotation, boolean isInverse) {
		Edge<AnnotatedValue,CfgSymbol> edge1 = new Edge(left,right,CfgSymbol.getAtomicOpen(annotation));
		Edge<AnnotatedValue,CfgSymbol> edge2 = new Edge(left,right,CfgSymbol.OPENPAREN);		
		
		graph.addEdge(edge1);
		graph.addEdge(edge2);
		if (isInverse == false) {
			originalGraph.addEdge(edge1);
			originalGraph.addEdge(edge2);
		}
	}

	@Override
	protected void addFieldClose(AnnotatedValue left, AnnotatedValue right,
			AnnotatedValue annotation, boolean isInverse) {
		Edge<AnnotatedValue,CfgSymbol> edge1 = new Edge(left,right,CfgSymbol.getAtomicClose(annotation));
		Edge<AnnotatedValue,CfgSymbol> edge2 = new Edge(left,right,CfgSymbol.CLOSEPAREN);
		
		graph.addEdge(edge1);
		graph.addEdge(edge2);
		
		if (isInverse == false) {
			originalGraph.addEdge(edge1);
			originalGraph.addEdge(edge2);
		}
	}

	@Override
	protected void addCallOpen(AnnotatedValue left, AnnotatedValue right,
			AnnotatedValue annotation, boolean isInverse) {
		if (!left.equals(right)) { 
			Edge<AnnotatedValue,CfgSymbol> edge = new Edge(left,right,CfgSymbol.LOCAL);
			graph.addEdge(edge);
			if (isInverse == false) {
				originalGraph.addEdge(edge);
			}
		}
	}

	@Override
	protected void addCallClose(AnnotatedValue left, AnnotatedValue right,
			AnnotatedValue annotation, boolean isInverse) {
		if (!left.equals(right)) { 
			
			Edge<AnnotatedValue,CfgSymbol> edge = new Edge(left,right,CfgSymbol.LOCAL);
			graph.addEdge(edge);
			if (isInverse == false) {
				originalGraph.addEdge(edge);
			}
		}
	}

	@Override
	protected void addLocal(AnnotatedValue left, AnnotatedValue right, boolean isInverse) {
		if (!left.equals(right)) { 
			Edge<AnnotatedValue,CfgSymbol> edge = new Edge(left,right,CfgSymbol.LOCAL);
			graph.addEdge(edge);
			if (isInverse == false) {
				originalGraph.addEdge(edge);
			}
		}
	}

	@Override
	protected void printHeaderString() {
		System.out.println("Printing fields graph");
	}
	
	/*
	protected void dynamicClosure(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Set<Edge<AnnotatedValue, CfgSymbol>> visitedEdges) {
		int count=0;
		while (!queue.isEmpty()) {
			System.out.println("queue.size: "+queue.size());
			Edge<AnnotatedValue,CfgSymbol> curr = queue.remove();
			// System.out.println("Current popped edge: "+curr);
			// Invariant: curr is LOCAL
			for (Edge<AnnotatedValue,CfgSymbol> next : graph.getEdgesFrom(curr.getTarget())) {
				addTransitiveLocalEdge(curr,next,queue,visitedEdges);
			}
			
			//for (Edge<AnnotatedValue,CfgSymbol> prev : graph.getEdgesInto(curr.getSource())) {
			//	addTransitiveLocalEdge(prev,curr,queue,visitedEdges);
			//}
			
						
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
	*/
	
	private boolean hasAtomicOpenPredecessor(AnnotatedValue source) {
		for (Edge<AnnotatedValue,CfgSymbol> prevprev : graph.getEdgesInto(source)) {
			if (prevprev.getLabel() instanceof AtomicOpenParen) {
				return true;
			}
		}
		return false;
	}
	
	/*
	private void addTransitiveLocal(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Set<Edge<AnnotatedValue, CfgSymbol>> visitedEdges,
			AnnotatedValue left, AnnotatedValue right) {
		if (graph.hasEdge(left,right,CfgSymbol.LOCAL)) return; // edge is already in the graph.
		for (Edge<AnnotatedValue,CfgSymbol> prev : graph.getEdgesInto(left)) {
			if (prev.getLabel() instanceof AtomicOpenParen) {
				addLocalEdge(queue,visitedEdges,left,right);
			}
			else if (prev.getLabel() == CfgSymbol.LOCAL) {
				if (hasAtomicOpenPredecessor(prev.getSource())) {
					addLocalEdge(queue,visitedEdges,prev.getSource(),right);
				}
			}
		}
		if (!skipAddEdge(left,right)) 
			graph.addEdge(left,right,CfgSymbol.LOCAL);
	}
	*/
	
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

	/* === BUILDING THE PT GRAPH ==== */
	
	@Override
	protected boolean addTransitiveEdgeToQueue(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Edge<AnnotatedValue, CfgSymbol> e1, Edge<AnnotatedValue, CfgSymbol> e2) {		
		if (e2.getLabel() == CfgSymbol.LOCAL) {
			Edge<AnnotatedValue, CfgSymbol> newEdge = 
					new Edge<AnnotatedValue,CfgSymbol>(e1.getSource(),e2.getTarget(),CfgSymbol.LOCAL);
			if (!ptGraph.hasEdge(e1.getSource(),e2.getTarget(),CfgSymbol.LOCAL) && 
					UtilFuncs.typeCompatible(e1.getSource().getType(),e2.getTarget().getType())) {  
				ptGraph.addEdge(e1.getSource(),e2.getTarget(),CfgSymbol.LOCAL);
				queue.add(newEdge);
				//System.out.println("ADDED TRANSITIVE EDGE TO QUEUE: "+newEdge+" FOR EDGES "+e1+" AND "+e2);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void addEdgeToQueue(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Edge<AnnotatedValue, CfgSymbol> e) {
		if (e.getLabel() == CfgSymbol.LOCAL && UtilFuncs.typeCompatible(e.getSource().getType(),e.getTarget().getType())) { 			
			ptGraph.addEdge(e.getSource(),e.getTarget(),e.getLabel());
			Edge<AnnotatedValue, CfgSymbol> newEdge = new Edge(e.getSource(),e.getTarget(),e.getLabel());
			queue.add(newEdge);
			//System.out.println("ADDED EDGE TO QUEUE: "+newEdge);
		}
	}
	
	
	
	
	@Override
	protected boolean isFieldWrite(Edge<AnnotatedValue,CfgSymbol> e1) {	
		return false;
	}

	@Override
	protected void addAllTransitiveEdges() {
		// TODO Auto-generated method stub
		Map<AnnotatedValue,Set<AnnotatedValue>> revNodeToRep = new HashMap<AnnotatedValue,Set<AnnotatedValue>>();
		for (AnnotatedValue X : nodeToRep.keySet()) {
			//System.out.println("Adding "+X+" to "+nodeToRep.get(X));
			UtilFuncs.addToMap(revNodeToRep,nodeToRep.get(X),X);
		}			
		
		Map<AnnotatedValue,Set<AnnotatedValue>> incomingMap = new HashMap<AnnotatedValue,Set<AnnotatedValue>>();
		Map<AnnotatedValue,Set<AnnotatedValue>> outgoingMap = new HashMap<AnnotatedValue,Set<AnnotatedValue>>();
		
		collectTransitiveSourceAndTargetNodes(incomingMap,outgoingMap);
		
		System.out.println("Started addAllTransitiveEdges");
		
		Graph<AnnotatedValue,CfgSymbol> uncollapsedTransitiveEdges = new Graph<AnnotatedValue,CfgSymbol>();
		
		int i=0;
		int k=0;
		int numNodes = transitiveEdges.getNodes().size();
		HashSet<Pair> visited = new HashSet<Pair>();
		for (AnnotatedValue v : transitiveEdges.getNodes()) {
			i += transitiveEdges.getEdgesFrom(v).size();
			//System.out.println("Added "+i+"edges for node "+k++ + " out of "+numNodes);
			k++;
			for (Edge<AnnotatedValue,CfgSymbol> e : transitiveEdges.getEdgesFrom(v)) {
				//System.out.println(" Edge: "+e);
				AnnotatedValue source = e.getSource();
				AnnotatedValue target = e.getTarget();
				Set<AnnotatedValue> sSet = revNodeToRep.get(source);
				if (sSet == null) sSet = revNodeToRep.get(nodeToRep.get(source));
				Set<AnnotatedValue> tSet = revNodeToRep.get(target);
				if (tSet == null) tSet = revNodeToRep.get(nodeToRep.get(target));
				
				if (visited.contains(new Pair(sSet,tSet))) continue;
				visited.add(new Pair(sSet,tSet));
				
				if (sSet.size() > 10000 && sSet.containsAll(tSet)) continue;
				if (tSet.size() > 10000 && tSet.containsAll(sSet)) continue;
				
				// System.out.println("sSet size: "+sSet.size() +" and tSet size: "+tSet.size()+" at "+k+" out of "+numNodes);
				
				
				for (AnnotatedValue s : sSet) {
					for (AnnotatedValue t : tSet) {
						if (skipAddEdge(s,t)) continue;
						if ((outgoingMap.get(s) != null) && (incomingMap.get(t) != null) && 
								intersect(outgoingMap.get(s),incomingMap.get(t)).size() != 0) {
							//originalGraph.addEdge(new Edge(s,t,CfgSymbol.LOCAL));
							if (s.getId() == 25771) System.out.println("TR EDGE: "+t);
							if (s.equals(t)) continue;
							uncollapsedTransitiveEdges.addEdge(new Edge(s,t,CfgSymbol.LOCAL));
						}
					}
				}
				
			}
		}
		
		transitiveEdges = uncollapsedTransitiveEdges;
		
		System.out.println("transitiveEdges has "+i+ " edges! ");
		
	}	
	
}

class Pair {
	Set<AnnotatedValue> sSet;
	Set<AnnotatedValue> tSet;
	
	Pair(Set<AnnotatedValue> sSet, Set<AnnotatedValue> tSet) {
		this.sSet = sSet;
		this.tSet = tSet;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Pair) {
			Pair o = (Pair) other;
			return sSet == o.sSet && tSet == o.tSet;
		}
		return false;
	}
	@Override
	public int hashCode() {
		return sSet.hashCode() + tSet.hashCode();
	}
	
}
