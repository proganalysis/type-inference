package edu.rpi.reimutils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import edu.rpi.AnnotatedValue;
import edu.rpi.InferenceTransformer;

public class CallConstraintGraph extends ConstraintGraph {

	public CallConstraintGraph(InferenceTransformer transformer) {
		super(transformer);
		// TODO Auto-generated constructor stub
	}
	private static AnnotatedValue SUBTYPE = new AnnotatedValue("Subtype.",null,AnnotatedValue.Kind.COMPONENT,null);
	
	@Override
	protected void addFieldOpen(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation) {
		if (!left.equals(right)) graph.addEdge(left,right,CfgSymbol.LOCAL);
	}
	@Override
	protected void addFieldClose(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation) {
		if (!left.equals(right)) graph.addEdge(left,right,CfgSymbol.LOCAL);
	}
	@Override
	protected void addCallOpen(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation) {
		graph.addEdge(left,right,CfgSymbol.getAtomicOpen(annotation));
		graph.addEdge(left,right,CfgSymbol.OPENPAREN);
	}
	@Override
	protected void addCallClose(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation) {
		graph.addEdge(left,right,CfgSymbol.getAtomicClose(annotation));
		graph.addEdge(left,right,CfgSymbol.CLOSEPAREN);
	}
	@Override
	protected void addLocal(AnnotatedValue left, AnnotatedValue right) {
		if (!left.equals(right)) graph.addEdge(left,right,CfgSymbol.LOCAL);
	}
	@Override
	protected void printHeaderString() {
		System.out.println("Printing calls graph");
	}
	
	@Override
	protected boolean skipAddEdge(AnnotatedValue left, AnnotatedValue right) {		
		if (SCCUtilities.isStaticField(left) && SCCUtilities.isStaticField(right)) {
			return false;
		}
		else if (SCCUtilities.isStaticField(left) != SCCUtilities.isStaticField(right)) {
			return true;
		}
		else if (!left.getEnclosingMethod().equals(right.getEnclosingMethod())) 
			return true;
		else
			return false;
	}
	
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
				
	@Override
	protected void addTransitiveEdgeToQueue(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
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
				// System.out.println("ADDED EDGE TO QUEUE: "+newEdge+" FOR EDGES "+e1+" AND "+e2);
			}
		}
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
			// System.out.println("ADDED EDGE TO QUEUE: "+newEdge);
		}
	}
		
}
