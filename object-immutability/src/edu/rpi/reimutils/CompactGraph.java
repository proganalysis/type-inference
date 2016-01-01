package edu.rpi.reimutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

class IntEdge<N> {
	N source;
	CfgSymbol label;
	public IntEdge(N source, CfgSymbol label) {
		this.source = source;
		this.label = label;
	}
	@Override
	public boolean equals(Object other) {		
		if (!(other instanceof IntEdge)) return false;
		IntEdge o = (IntEdge) other;
		boolean result = o.source.equals(source) && o.label == label;
		// System.out.println("Objects "+toString()+" and "+other.toString()+" have equals="+result);
		return result;
	}
	@Override
	public int hashCode() {
		return source.hashCode()+label.hashCode();
	}
	
	public N getSource() {
		return source;
	}
	public CfgSymbol getLabel() {
		return label;
	}
}

public class CompactGraph<N> {
	// HashSet<N> nodes;
	// Rep invariant: nodes contains no nulls and no duplicates (trivially enforced by Hashset)
	// 				  edges contains no nulls and no duplicate edges, according to definition of "equals"
	//				  all sources and targets of edges must be in nodes
	
	HashMap<N,HashSet<N>> callParents;
	HashMap<N,HashSet<N>> allParents;
	
	
	public CompactGraph() {
//		nodes = new HashSet<N>();
//		edges = new HashMap<N,HashSet<Edge<N,L>>>();
		callParents = new HashMap<N,HashSet<N>>();
		allParents = new HashMap<N,HashSet<N>>();
	}
	
	
	// requires: nothing
	// modifies: this
	// effects: adds node to this Graph's nodes
	// returns: true if node was added, false if it was not (because there already exist the same node
	//public boolean addNode(N node) {
	//	return nodes.add(node);
	//}
	
	//public boolean hasNode(N node) {
	//	return nodes.contains(node);
	//}

	// effects: adds an edge from source and target in Graph
	//          if source or target not in graph, they are added
	//          if edge already in graph, addEdge has no effect
	// requires: hasEdge(source,target,label) is false
	public boolean addEdge(N source, N target, CfgSymbol label) {
		//if (!nodes.contains(source)) nodes.add(source);
		//if (!nodes.contains(target)) nodes.add(target);
		
		HashSet<N> all = allParents.get(target);
		HashSet<N> call = callParents.get(target);
		boolean result;
		
		if (label == CfgSymbol.CLOSEPAREN || label == CfgSymbol.LOCAL) {
			if (all == null) {
				all = new HashSet<N>();
				allParents.put(target,all);
			}
			result = all.add(source);
			if (call != null) {
				call.remove(source);
				if (call.size() == 0) {
					callParents.remove(target);
				}
			}
		}
		//else {
		//	// assert openparen
		else if (label == CfgSymbol.OPENPAREN) {
			if (call == null) {
				call = new HashSet<N>();
				callParents.put(target,call);
		    }
		    call.add(source);
		    result = call.add(source);
		}
		else {
			System.out.println("This shoudl not happen!!! "+label);
			return false;
		}
		
		
		return result;
		
		/*
		HashSet<N> hs2 = parents.get(target);
		if (hs2 == null) {
			hs2 = new HashSet<N>();
			parents.put(target,hs2);
		}
		return hs2.add(source);
		*/
		
		// System.out.println("Adding "+source+","+target+","+label+" is "+result);
	}
	
	public boolean hasEdge(N source, N target, CfgSymbol label) {
		
		HashSet<N> all = allParents.get(target);
		if (all != null && all.contains(source)) return true;
		
		
		if (label == CfgSymbol.CLOSEPAREN || label == CfgSymbol.LOCAL) {
			return false;
		}
		else if (label == CfgSymbol.OPENPAREN) {
			// assert label == CfgSymbol.OPENPAREN
			HashSet<N> call = callParents.get(target);
			if (call != null && call.contains(source)) return true;
			else return false;
		}
		else {
			System.out.println("This sould not happen! Got label "+label);
			return true;
		}
		
	
	}
	
	
	// requires: returns a new list of the nodes 	
	//public List<N> getNodes() {
	//	return new ArrayList<N>(nodes);
	//}
	/*
	// returns: a list of Strings, where each String represents the in format: target(label)
	//          empty list if there is no node source or source has no targets
	public List<Edge<N,L>> getEdgesFrom(N source) {
		List<Edge<N,L>> listEdges = new ArrayList<Edge<N,L>>();
		HashSet<Edge<N,L>> hs = edges.get(source);
		if (hs == null) return listEdges;
		for (Iterator<Edge<N,L>> it=hs.iterator(); it.hasNext();) {
			Edge<N,L> e = it.next();
			listEdges.add(e);
			// listEdges.add(e.target+"("+e.label+")");
		}
		assert(listEdges != null);
		return listEdges;
	}
	*/
	// returns: the set of edges ending at target
	public List<Edge<N,CfgSymbol>> getEdgesInto(N target) {
		List<Edge<N,CfgSymbol>> listEdges = new ArrayList<Edge<N,CfgSymbol>>();
		HashSet<N> hs = allParents.get(target);
		HashSet<N> hs2 = callParents.get(target);
		if (hs == null && hs2 == null) return listEdges; 
		if (hs != null) {
			for (Iterator<N> it=hs.iterator(); it.hasNext();) {
				N source = it.next();
				Edge<N,CfgSymbol> edge = new Edge<N,CfgSymbol>(source,target,CfgSymbol.CLOSEPAREN);
				listEdges.add(edge);
			}
		}
		if (hs2 != null) { 			
			for (Iterator<N> it=hs2.iterator(); it.hasNext();) {
				N source = it.next();
				Edge<N,CfgSymbol> edge = new Edge<N,CfgSymbol>(source,target,CfgSymbol.OPENPAREN);
				listEdges.add(edge);
			}
		}
		/*
		HashSet<N> hs2 = parents.get(target);
		if (hs2 == null) return listEdges;
		for (Iterator<N> it=hs2.iterator(); it.hasNext();) {
			N e = it.next();
			Edge<N,L> edge = new Edge<N,L>(e,target,null);
			listEdges.add(edge);
		}
		*/
		
		assert(listEdges != null);
		return listEdges;
	}
	
	public void removeEdge(N source, N target, CfgSymbol label) {
		removeFromMap(allParents,source,target);
		removeFromMap(callParents,source,target);
	}
	
	// if not present, no effect
	private void removeFromMap(HashMap<N,HashSet<N>> hm, N source, N target) {
		HashSet<N> set = hm.get(target);
		if (set != null && set.contains(source)) {
			set.remove(source);
			if (set.size() == 0) {
				System.out.println("REMOVED map for: "+source+"  --- > "+target);
				hm.remove(target);
			}
		}
	}
	
}

