package edu.rpi.reimutils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.rpi.AnnotatedValue;
import edu.rpi.InferenceTransformer;

public class CallConstraintGraph extends ConstraintGraph {

	Graph<AnnotatedValue,CfgSymbol> fieldWrites = new Graph<AnnotatedValue,CfgSymbol>();
	
	public CallConstraintGraph(InferenceTransformer transformer) {
		super(transformer);
		// TODO Auto-generated constructor stub
	}
	private static AnnotatedValue SUBTYPE = new AnnotatedValue("Subtype.",null,AnnotatedValue.Kind.COMPONENT,null);
	
	@Override
	protected void addFieldOpen(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation, boolean isInverse) {
		if (!left.equals(right)) { 
			Edge<AnnotatedValue, CfgSymbol> edge = new Edge(left,right,CfgSymbol.LOCAL);
			graph.addEdge(edge);
			fieldWrites.addEdge(edge);
			if (isInverse == false) {
				originalGraph.addEdge(edge);
			}
		}
	}
	@Override
	protected void addFieldClose(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation, boolean isInverse) {
		if (!left.equals(right)) { 
			Edge<AnnotatedValue, CfgSymbol> edge = new Edge(left,right,CfgSymbol.LOCAL);
			graph.addEdge(edge);
			if (isInverse == false) {
				originalGraph.addEdge(edge);
			}
		}
	}
	@Override
	protected void addCallOpen(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation, boolean isInverse) {		
		Edge<AnnotatedValue, CfgSymbol> edge1 = new Edge(left,right,CfgSymbol.getAtomicOpen(annotation));
		Edge<AnnotatedValue, CfgSymbol> edge2 = new Edge(left,right,CfgSymbol.OPENPAREN);		
		graph.addEdge(edge1);
		graph.addEdge(edge2);
		if (isInverse == false) {
			originalGraph.addEdge(edge1);
			originalGraph.addEdge(edge2);
		}
	}
	@Override
	protected void addCallClose(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation, boolean isInverse) {
		
		Edge<AnnotatedValue, CfgSymbol> edge1 = new Edge(left,right,CfgSymbol.getAtomicClose(annotation));
		Edge<AnnotatedValue, CfgSymbol> edge2 = new Edge(left,right,CfgSymbol.CLOSEPAREN);
		
		graph.addEdge(edge1);
		graph.addEdge(edge2);
		
		if (isInverse == false) {
			originalGraph.addEdge(edge1);
			originalGraph.addEdge(edge2);
		}
	}
	@Override
	protected void addLocal(AnnotatedValue left, AnnotatedValue right, boolean isInverse) {
		if (!left.equals(right)) { 
			
			//if (left.getEnclosingMethod() != right.getEnclosingMethod()) {
			//	System.out.println("SOMETHING WRONG! "+left+" AND "+right);
			//}
			
			Edge<AnnotatedValue, CfgSymbol> edge = new Edge(left,right,CfgSymbol.LOCAL);
			graph.addEdge(edge);
			if (isInverse == false) {
				originalGraph.addEdge(edge);
			}
		}
	}
	@Override
	protected void printHeaderString() {
		System.out.println("Printing calls graph");
	}
	
	
	
	@Override
	protected boolean skipAddEdge(AnnotatedValue left, AnnotatedValue right) {		
		if (SCC.isStaticField(left) && SCC.isStaticField(right)) {
			return false;
		}
		else if (SCC.isStaticField(left) != SCC.isStaticField(right)) {
			return true;
		}
		else if (!left.getEnclosingMethod().equals(right.getEnclosingMethod())) 
			return true;
		else
			return false;
	}
	/*
	private void edgePlusLocal(CfgSymbol label, Edge<AnnotatedValue,CfgSymbol> edge) {
		if (edge.getLabel() == label) {
			for (Edge<AnnotatedValue,CfgSymbol> next : graph.getEdgesFrom(edge.getTarget())) {
				if (next.getLabel() == CfgSymbol.LOCAL)
					graph.addEdge(edge.getSource(),next.getTarget(),label);
			}
			for (Edge<AnnotatedValue,CfgSymbol> prev : graph.getEdgesInto(edge.getSource())) {
				if (prev.getLabel() == CfgSymbol.LOCAL)
					graph.addEdge(prev.getSource(),edge.getTarget(),label);
			}
		}
	}
	*/
				
	@Override
	protected boolean addTransitiveEdgeToQueue(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Edge<AnnotatedValue, CfgSymbol> e1, Edge<AnnotatedValue, CfgSymbol> e2) {
		CfgSymbol label = e1.getLabel().finalConcat(e2.getLabel());
		if (label != null) {
			Edge<AnnotatedValue, CfgSymbol> newEdge = 
					new Edge<AnnotatedValue,CfgSymbol>(e1.getSource(),e2.getTarget(),label);
			if (!ptGraph.hasEdge(e1.getSource(),e2.getTarget(),label)) {
			    ptGraph.addEdge(e1.getSource(),e2.getTarget(),label);			    
			//if (!((tryPtGraph.get(e1.getSource()) != null) && tryPtGraph.get(e1.getSource()).contains(e2.getTarget()))) {
			//	addToMap(tryPtGraph,e1.getSource(),e2.getTarget());
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
		if (!(e.getLabel() instanceof AtomicOpenParen) && !(e.getLabel() instanceof AtomicCloseParen)) {
			Edge<AnnotatedValue, CfgSymbol> newEdge = 
					new Edge<AnnotatedValue,CfgSymbol>(e.getSource(),e.getTarget(),e.getLabel());
			ptGraph.addEdge(e.getSource(),e.getTarget(),e.getLabel());
			// addToMap(tryPtGraph,e.getSource(),e.getTarget());
			queue.add(newEdge);
			//System.out.println("ADDED EDGE TO QUEUE: "+newEdge);
		}
	}
	
	@Override
	protected boolean isFieldWrite(Edge<AnnotatedValue,CfgSymbol> e) {		
		if (fieldWrites.hasEdge(e.getSource(),e.getTarget(),e.getLabel())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected void addAllTransitiveEdges() {
		// TODO Auto-generated method stub
		Map<AnnotatedValue,Set<AnnotatedValue>> revNodeToRep = new HashMap<AnnotatedValue,Set<AnnotatedValue>>();
		for (AnnotatedValue X : nodeToRep.keySet()) {
			//System.out.println("Adding "+X+" to "+nodeToRep.get(X));
			UtilFuncs.addToMap(revNodeToRep,nodeToRep.get(X),X);
		}			
		
		Map<AnnotatedValue, Set<AnnotatedValue>> incomingMap = new HashMap<AnnotatedValue, Set<AnnotatedValue>>();
		Map<AnnotatedValue, Set<AnnotatedValue>> outgoingMap = new HashMap<AnnotatedValue, Set<AnnotatedValue>>();
		
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
				
				//System.out.println("sSet size: "+sSet.size() +" and tSet size: "+tSet.size()+" at "+k+" out of "+numNodes);
				
				
				for (AnnotatedValue s : sSet) {
					for (AnnotatedValue t : tSet) {
						if (skipAddEdge(s,t)) continue;
						if ((outgoingMap.get(s) != null) && (incomingMap.get(t) != null)) {
							
							if (s.equals(t)) continue;
							
							Set<AnnotatedValue> annoInfo = intersect(outgoingMap.get(s),incomingMap.get(t));
							if (annoInfo.size() == 0) continue;
							//originalGraph.addEdge(new Edge(s,t,CfgSymbol.LOCAL));
							
							uncollapsedTransitiveEdges.addEdge(new Edge(s,t,CfgSymbol.LOCAL));
							// TODO: This is a hack. mark a trans edge as fieldWrite...
							for (AnnotatedValue info : annoInfo) {
								for (Edge<AnnotatedValue,CfgSymbol> in : originalGraph.getEdgesFrom(s)) {
									if (!((in.getLabel() instanceof AtomicOpenParen) && 
											((AtomicOpenParen) in.getLabel()).getInfo() == info)) continue;
									for (Edge<AnnotatedValue,CfgSymbol> out : originalGraph.getEdgesFrom(t)) {
										if (!((out.getLabel() instanceof AtomicOpenParen) && 
												((AtomicOpenParen) out.getLabel()).getInfo() == info)) continue;
										// in: A - (_i -> X and out: Y -)_i -> B
										if (in.getTarget().getKind() == AnnotatedValue.Kind.PARAMETER &&
												(out.getTarget().getKind() == AnnotatedValue.Kind.PARAMETER || 
												out.getTarget().getKind() == AnnotatedValue.Kind.THIS)) {
											if (!skipAddEdge(s,t) && !s.equals(t)) { 
												// System.out.println("ADDED fieldWrite: "+s+" to "+t);
												fieldWrites.addEdge(new Edge(s,t,CfgSymbol.LOCAL));
											}
										}
									}
								}
							}
								
							
						}
					}
				}
				
			}
		}
		
		transitiveEdges = uncollapsedTransitiveEdges;
		
		System.out.println("transitiveEdges has "+i+ " edges! ");
		
	}	
			
}
