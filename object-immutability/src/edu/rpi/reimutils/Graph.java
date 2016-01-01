package edu.rpi.reimutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Graph<N,L> {
	HashSet<N> nodes;
	HashMap<N,HashSet<Edge<N,L>>> edges;
	// Rep invariant: nodes contains no nulls and no duplicates (trivially enforced by Hashset)
	// 				  edges contains no nulls and no duplicate edges, according to definition of "equals"
	//				  all sources and targets of edges must be in nodes
	HashMap<N,HashSet<Edge<N,L>>> parents;
	
	
	public Graph() {
		nodes = new HashSet<N>();
		edges = new HashMap<N,HashSet<Edge<N,L>>>();
		parents = new HashMap<N,HashSet<Edge<N,L>>>();
	}
	
	
	// requires: nothing
	// modifies: this
	// effects: adds node to this Graph's nodes
	// returns: true if node was added, false if it was not (because there already exist the same node
	public boolean addNode(N node) {
		return nodes.add(node);
	}
	
	public boolean hasNode(N node) {
		return nodes.contains(node);
	}

	// effects: adds an edge from source and target in Graph
	//          if source or target not in graph, they are added
	//          if edge already in graph, addEdge has no effect
	public boolean addEdge(N source, N target, L label) {
		if (!nodes.contains(source)) nodes.add(source);
		if (!nodes.contains(target)) nodes.add(target);
		
		Edge<N,L> e = new Edge<N,L>(source,target,label);
		HashSet<Edge<N,L>> hs = edges.get(source);
		if (hs == null) {
			hs = new HashSet<Edge<N,L>>();
			edges.put(source,hs);
		}
		boolean result = hs.add(e);
		// Now we have to add to the parent set
		HashSet<Edge<N,L>> hs2 = parents.get(target);
		if (hs2 == null) {
			hs2 = new HashSet<Edge<N,L>>();
			parents.put(target,hs2);
		}
		hs2.add(e);
		// System.out.println("Adding "+source+","+target+","+label+" is "+result);
		return result;
	}
	
	public boolean hasEdge(N source, N target, L label) {
		HashSet<Edge<N,L>> hs = edges.get(source);
		if (hs == null) return false;
		else {
			return hs.contains(new Edge<N,L>(source,target,label));
		}
	}
	
	// requires: returns a new list of the nodes 	
	public List<N> getNodes() {
		return new ArrayList<N>(nodes);
	}
	
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
	
	// returns: the set of edges ending at target
	public List<Edge<N,L>> getEdgesInto(N target) {
		List<Edge<N,L>> listEdges = new ArrayList<Edge<N,L>>();
		HashSet<Edge<N,L>> hs2 = parents.get(target);
		if (hs2 == null) return listEdges;
		for (Iterator<Edge<N,L>> it=hs2.iterator(); it.hasNext();) {
			Edge<N,L> e = it.next();
			listEdges.add(e);
		}
		assert(listEdges != null);
		return listEdges;
	}
}
