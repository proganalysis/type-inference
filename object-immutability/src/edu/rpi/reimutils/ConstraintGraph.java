package edu.rpi.reimutils;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.io.File;

import soot.ArrayType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SourceLocator;
import soot.Type;
import checkers.inference.reim.quals.Readonly;
import checkers.inference.reim.quals.Polyread;
import edu.rpi.AnnotatedValue;
import edu.rpi.AnnotationUtils;
import edu.rpi.Constraint;
import edu.rpi.InferenceTransformer;
import edu.rpi.reim.ReimTransformer;

public abstract class ConstraintGraph {

	// Rep:
	protected Graph<AnnotatedValue,CfgSymbol> graph; 
	protected Map<AnnotatedValue, AnnotatedValue> nodeToRep;
	protected CompactGraph<AnnotatedValue> ptGraph;
	
	protected Graph<AnnotatedValue,CfgSymbol> originalGraph; // This is the original graph, without inverse edges
	protected Graph<AnnotatedValue,CfgSymbol> transitiveEdges; // This is the set of edges added during dynamicClosure
	
	
	// Utilities:
	protected InferenceTransformer reimTransformer;
	protected LibraryUtilities libraryUtils;
	
	
	// Special array "annotation".
	private static AnnotatedValue ARRAY = new AnnotatedValue("Arrays.",null,AnnotatedValue.Kind.COMPONENT,null);
	
	private static AnnotatedValue SUBTYPE = new AnnotatedValue("Subtype.",null,AnnotatedValue.Kind.COMPONENT,null);
	
	static AnnotatedValue LIB = new AnnotatedValue("Lib.",null,AnnotatedValue.Kind.COMPONENT,null);
	
	// Rep invariant: graph nodes are AnnotatedValues of Kind different than METH_ADAPT or FIELD_ADAPT
	// Rep invariant: Set of graph nodes = set of values in nodeToRep
	// Rep invariant: set of keys in nodeToRep = set of original variables in program
	// Rep invariant: set of nodes in transitiveEdges is set of nodes in graph
	
	public ConstraintGraph(InferenceTransformer transformer) {
		reimTransformer = transformer;
		graph = new Graph<AnnotatedValue,CfgSymbol>();
		libraryUtils = new LibraryUtilities(this);
		nodeToRep = new HashMap<AnnotatedValue, AnnotatedValue>();
		
		originalGraph = new Graph<AnnotatedValue,CfgSymbol>();
		transitiveEdges = new Graph<AnnotatedValue,CfgSymbol>();
		
	}
	protected abstract void addFieldOpen(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation, boolean isInverse);
	protected abstract void addFieldClose(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation, boolean isInverse);
	protected abstract void addCallOpen(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation, boolean isInverse);
	protected abstract void addCallClose(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation, boolean isInverse);
	protected abstract void addLocal(AnnotatedValue left, AnnotatedValue right, boolean isInverse);
	
	private void processCallClose(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation,
			AnnotatedValue rightHandSide) {
		addCallClose(left,right,annotation,false);
		if (!UtilFuncs.isReadonly(rightHandSide,reimTransformer))
			addCallOpen(right,left,annotation,true);
	}
	private void processCallOpen(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation,
			AnnotatedValue rightHandSide) {
		addCallOpen(left,right,annotation,false);
		if (!UtilFuncs.isReadonly(rightHandSide,reimTransformer))
			addCallClose(right,left,annotation,true);
	}
	void processFieldClose(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation,
			AnnotatedValue rightHandSide) {
		addFieldClose(left,right,annotation,false);
		if (!UtilFuncs.isReadonly(rightHandSide,reimTransformer))
			addFieldOpen(right,left,annotation,true);
	}
	void processFieldOpen(AnnotatedValue left, AnnotatedValue right, AnnotatedValue annotation,
			AnnotatedValue rightHandSide) {
		addFieldOpen(left,right,annotation,false);
		if (!UtilFuncs.isReadonly(rightHandSide,reimTransformer))
			addFieldClose(right,left,annotation,true);
	}

	void processLocal(AnnotatedValue left, AnnotatedValue right) {
		addLocal(left,right,false);
		if (!UtilFuncs.isReadonly(right,reimTransformer))
			addLocal(right,left,true);
	}
	
	private boolean skipConstraint(Constraint c) {
		boolean result = false;
		
		// kind = 0 is SUB constraint, kind = 1 is EQU constraint
		// TODO: have to refactor to get rid of magic numbers
		// Here, we skip EQU constraints (Wei needs them to handle arrays. We don't.)
		if (c.getKind() == 1) return true;
		
		// Ignore constraints on simple types:
		if (!(c.getLeft() instanceof AnnotatedValue.AdaptValue) && 
				!(c.getLeft().getType() instanceof RefLikeType)) {
			return true;			
		}
		if (!(c.getRight() instanceof AnnotatedValue.AdaptValue) && 
				!(c.getRight().getType() instanceof RefLikeType)) {
			return true;
		}
		
		
		if (libraryUtils.isLibraryAdaptConstraint(c,reimTransformer)) {
			return true;
		}
		
		return result;		
	}
	
	/*
	 * @param constraints set of Plain (or ReIm?) constraints
	 * @effects fills in graph g
	 * @modifies this.g
	 */	
	public void createNewGraph() {
		Set<Constraint> constraints = reimTransformer.getConstraints();
		Set<Constraint> startConstraints = new HashSet<Constraint>();
		for (Constraint c : constraints) {			
			
				// Handle x.start(). Postpone. handle at the end of graph creat.
				if (isStartConstraint(c)) {	startConstraints.add(c); continue; }
			
				if (skipConstraint(c)) continue;
							
				// Handle subtype constraints: 
				if ((c.getLeft().getKind() == AnnotatedValue.Kind.PARAMETER) && (c.getRight().getKind() == AnnotatedValue.Kind.PARAMETER) ||
						(c.getLeft().getKind() == AnnotatedValue.Kind.THIS) && (c.getRight().getKind() == AnnotatedValue.Kind.THIS) ) {
					processCallOpen(c.getLeft(),c.getRight(),SUBTYPE,c.getRight());
					continue;
				}
				else if ((c.getLeft().getKind() == AnnotatedValue.Kind.RETURN) && (c.getRight().getKind() == AnnotatedValue.Kind.RETURN)) {
					processCallClose(c.getLeft(),c.getRight(),SUBTYPE,c.getRight());
					continue;
				}
								
				// System.out.println("Handling constraint "+c);
				AnnotatedValue left = c.getLeft();
				AnnotatedValue right = c.getRight();
				
				
				if (left instanceof AnnotatedValue.AdaptValue) {
					// We have a field read or method return
					AnnotatedValue.AdaptValue leftAdapt = (AnnotatedValue.AdaptValue) left;
					AnnotatedValue lhs = leftAdapt.getDeclValue();
					AnnotatedValue context = leftAdapt.getContextValue();
					
					assert (lhs.getKind() != AnnotatedValue.Kind.METH_ADAPT && lhs.getKind() != AnnotatedValue.Kind.FIELD_ADAPT);
					assert (right.getKind() != AnnotatedValue.Kind.METH_ADAPT && right.getKind() != AnnotatedValue.Kind.FIELD_ADAPT);
					
					if (left instanceof AnnotatedValue.FieldAdaptValue) {
						// Handling arrays. If array read, context is Array. Same for array writes.
						if (lhs.getKind() == AnnotatedValue.Kind.COMPONENT)
							lhs = ARRAY;
						processFieldClose(context,right,lhs,right);
					}
					else {
						assert (left instanceof AnnotatedValue.MethodAdaptValue);
						processCallClose(lhs,right,context,right);
					}
				}
				else if (right instanceof AnnotatedValue.AdaptValue) {
					// We have a field write or method call
					AnnotatedValue.AdaptValue rightAdapt = (AnnotatedValue.AdaptValue) right;
					AnnotatedValue rhs = rightAdapt.getDeclValue();
					AnnotatedValue context = rightAdapt.getContextValue();
					
					assert (rhs.getKind() != AnnotatedValue.Kind.METH_ADAPT && rhs.getKind() != AnnotatedValue.Kind.FIELD_ADAPT);
					assert (left.getKind() != AnnotatedValue.Kind.METH_ADAPT && left.getKind() != AnnotatedValue.Kind.FIELD_ADAPT);

					if (right instanceof AnnotatedValue.FieldAdaptValue) {						
						if (rhs.getKind() == AnnotatedValue.Kind.COMPONENT) 
							rhs = ARRAY;
						processFieldOpen(left,context,rhs,right);
					}
					else {
						assert (right instanceof AnnotatedValue.MethodAdaptValue);
						processCallOpen(left,rhs,context,right);
					}					
				}
				else { // is Local
					assert (right.getKind() != AnnotatedValue.Kind.METH_ADAPT && right.getKind() != AnnotatedValue.Kind.FIELD_ADAPT);
					assert (left.getKind() != AnnotatedValue.Kind.METH_ADAPT && left.getKind() != AnnotatedValue.Kind.FIELD_ADAPT);
					//System.out.println("=== Adding local edge? "+ left + " ---> "+right);
					processLocal(left,right);
				}
												
		}
		// processes thread stuff
		processStartConstraints(startConstraints);
		
		// creates constraints due to library calls
		libraryUtils.processLibraryCalls(graph);
	}
	
	protected boolean isStartConstraint(Constraint c) {
		if (c.getRight() instanceof AnnotatedValue.MethodAdaptValue) {
			AnnotatedValue decl = ((AnnotatedValue.MethodAdaptValue) c.getRight()).getDeclValue();
			if (decl.getEnclosingMethod().getName().equals("start") && 
					decl.getEnclosingMethod().getDeclaringClass().getName().equals("java.lang.Thread")) {
				//System.out.println("FOUND START CONSTRAINT "+c);
				return true;
			}
			else
				return false;
		}
		return false;
	}
	
	// Handles special cases such as r.start() -> r.run();
	// Must be called at the end of createNewGraph, on original nodes
	protected void processStartConstraints(Set<Constraint> startConstraints) {
		HashSet<AnnotatedValue> runThis = new HashSet<AnnotatedValue>();
		// Needs improvement!
		for (AnnotatedValue s : graph.getNodes()) {
			if (s.getEnclosingMethod() == null) continue;
			if (s.getEnclosingMethod().getName().equals("run") && s.getKind() == AnnotatedValue.Kind.THIS) {
				runThis.add(s);
			}
		}
		// TODO: Can we improve??? Not very precise... CHA-based resolution.
		for (Constraint c : startConstraints) {
			for (AnnotatedValue t : runThis) {
				AnnotatedValue context = ((AnnotatedValue.MethodAdaptValue) c.getRight()).getContextValue();
				if (UtilFuncs.typeCompatible(t.getEnclosingMethod().getDeclaringClass().getType(),c.getLeft().getType())) {
					processCallOpen(c.getLeft(),t,context, new AnnotatedValue.MethodAdaptValue(context,t));
					System.out.println("Just created a new CALL OPEN, from "+c.getLeft()+" to "+t);
				}
			}
		}
		
	}
	
	
	protected abstract void printHeaderString(); 

	// effects: prints ptGraph if ptFlag is true, prints constraint graph otherwise
	public void printGraph() {
		
		String outputDir = SourceLocator.v().getOutputDir();
		String fileName = getClass().toString().substring(getClass().toString().lastIndexOf(".")+1);
		fileName = fileName +".DUMP";
		PrintStream graphDump = null;
		
		try {
			graphDump = new PrintStream(outputDir + File.separator + fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Graph<AnnotatedValue,CfgSymbol> g = graph;
		//if (ptFlag) g = ptGraph; else g = graph;
		int numEdges = 0;
		printHeaderString(); 
		for (AnnotatedValue node : g.getNodes()) {
			List<Edge<AnnotatedValue,CfgSymbol>> edges = g.getEdgesFrom(node);
			for (Edge<AnnotatedValue,CfgSymbol> edge : edges) {
				graphDump.println(numEdges++ +" "+edge.toString());
			}
		}
		
		System.out.println("Total numEdges: "+numEdges+"\n");
		/*
		for (AnnotatedValue node : nodeToRep.keySet()) {
			if (node.getKind() == AnnotatedValue.Kind.ALLOC)
				System.out.println("NODE: "+node);
		}
		System.out.println("Total num keys: "+nodeToRep.keySet().size()+"\n\n");
		*/
	}
	
	
	public void printPtGraph() {
		Graph<AnnotatedValue,CfgSymbol> normalGraph = new Graph<AnnotatedValue,CfgSymbol>();
		int numEdges = 0;
		System.out.println("Printing points-to graph:");
		for (AnnotatedValue n : nodeToRep.keySet()) {
			List<Edge<AnnotatedValue,CfgSymbol>> edges = ptGraph.getEdgesInto(n);
			for (Edge<AnnotatedValue,CfgSymbol> edge : edges) {
				normalGraph.addEdge(new Edge<AnnotatedValue,CfgSymbol>(edge.getSource(),edge.getTarget(),edge.getLabel()));
			}
		}
		
		for (AnnotatedValue node : normalGraph.getNodes()) {
			List<Edge<AnnotatedValue,CfgSymbol>> edges = normalGraph.getEdgesFrom(node);
			for (Edge<AnnotatedValue,CfgSymbol> edge : edges) {
				System.out.println(numEdges++ +" "+edge.toString());
			}
		}
		
		System.out.println("Total numEdges: "+numEdges+"\n");
		/*
		for (AnnotatedValue node : nodeToRep.keySet()) {
			if (node.getKind() == AnnotatedValue.Kind.ALLOC)
				System.out.println("NODE: "+node);
		}
		System.out.println("Total num keys: "+nodeToRep.keySet().size()+"\n\n");
		*/
	}
	
	/*
	 * @modifies graph
	 * @effects: computes transitive closure over CFG grammar embedded in g 
	 * @param: flag = true means we are doing Closure of calls; flag = false we are doing closure of fields
	 * 
	 */		
	public void closure() {
		// Initialize queue with original Local edges
		Queue<Edge<AnnotatedValue,CfgSymbol>> queue = new LinkedList<Edge<AnnotatedValue, CfgSymbol>>();
		Set<Edge<AnnotatedValue,CfgSymbol>> visitedEdges = new HashSet<Edge<AnnotatedValue, CfgSymbol>>();

		initialize(queue, visitedEdges);
		
		System.out.println("Queue.size: "+queue.size());
		
		dynamicClosure(queue, visitedEdges);
		
		addAllTransitiveEdges();
		
	}
	
	abstract protected void addAllTransitiveEdges();
	
	protected void initialize(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Set<Edge<AnnotatedValue, CfgSymbol>> visitedEdges) {
		for (AnnotatedValue node : graph.getNodes()) {
			List<Edge<AnnotatedValue,CfgSymbol>> edges = graph.getEdgesFrom(node);
			for (Edge<AnnotatedValue,CfgSymbol> edge : edges) {
				CfgSymbol theLabel = edge.getLabel();
				if (theLabel instanceof AtomicOpenParen) {
					for (Edge<AnnotatedValue,CfgSymbol> next : graph.getEdgesFrom(edge.getTarget())) {
						if (next.getLabel() == CfgSymbol.LOCAL) {
							if (!visitedEdges.contains(next)) {
								queue.add(next);
								visitedEdges.add(next);
								// System.out.println(" Added edge to queue: "+next);
							}
						}
					}
				}
			}			
		}
	}
	
	
	
	protected void dynamicClosure(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Set<Edge<AnnotatedValue, CfgSymbol>> visitedEdges) {
		int count=0;
		while (!queue.isEmpty()) {
			
			//System.out.println("queue.size: "+queue.size());
						
			Edge<AnnotatedValue,CfgSymbol> curr = queue.remove();

			
			//if (UtilFuncs.getNodeRepFromId(12359,nodeToRep).getId() == curr.getSource().getId()) {
			//	System.out.println("TOOK OFF "+curr);
			//	for (AnnotatedValue n : nodeToRep.keySet()) {				
			//		if (UtilFuncs.getNodeRepFromId(curr.getSource().getId(),nodeToRep).getId() == nodeToRep.get(n).getId()) {
			//			System.out.println("==== Node in equiv class of SOURCE: "+n);
			//		}
			//	}
			//}
			
			
			// Invariant: curr is LOCAL
			assert (curr.getLabel() == CfgSymbol.LOCAL);
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
						addAllTransitiveLocalEdges(queue,visitedEdges,prev.getSource(),next.getTarget());
						//System.out.println("Added ci LOCAL ci EDGE from: "+prev+" and "+next);
						count++;
						//addLocalEdge(queue,visitedEdges,prev.getSource(),next.getTarget(),CfgSymbol.LOCAL);
					}
				}
			}
		}
		//System.out.println("Counted "+count+" first level ci ci edges.");
		
		
	}
	
	private boolean hasAtomicOpenPredecessor(AnnotatedValue source) {
		for (Edge<AnnotatedValue,CfgSymbol> prevprev : graph.getEdgesInto(source)) {
			if (prevprev.getLabel() instanceof AtomicOpenParen) {
				return true;
			}
		}
		return false;
	}
	
	private void addAllTransitiveLocalEdges(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Set<Edge<AnnotatedValue, CfgSymbol>> visitedEdges,
			AnnotatedValue left, AnnotatedValue right) {
				
		if (!skipAddEdge(left,right)) {
			
			// THIS IS A TRANSITIVE EDGE...
			transitiveEdges.addEdge(new Edge(left,right,CfgSymbol.LOCAL));
			//if (UtilFuncs.getNodeRepFromId(12359,nodeToRep).getId() == left.getId()) {
			//	System.out.println("TR EDGE FROM "+left);
			//	System.out.println("in all... to "+right);
				
			// }
			
			
			if (!graph.hasEdge(left,right,CfgSymbol.LOCAL) && !skipAddEdge(left,right)) {
			
				//System.out.println("Added local edge from "+left+" to "+right);
			
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
				addLocal(left,right,true); // Want to add this only to graph
				
			}
		}
	}
		
	
	// Filter depends on kind of constraint graph
	abstract protected boolean skipAddEdge(AnnotatedValue left, AnnotatedValue right);
	
	
	// requires leftEdge.target == rightEdge.source
	// effects finds transitive edge left+right and adds it to queue and visited and graph g
	// modifies queue and visited
	protected void addTransitiveLocalEdge(Edge<AnnotatedValue,CfgSymbol> leftEdge, Edge<AnnotatedValue,CfgSymbol> rightEdge,
			Queue<Edge<AnnotatedValue,CfgSymbol>> queue, Set<Edge<AnnotatedValue,CfgSymbol>> visited) { 
		assert (leftEdge.getTarget().equals(rightEdge.getSource()));

		AnnotatedValue X = leftEdge.getSource();
		CfgSymbol label1 = leftEdge.getLabel();		
		AnnotatedValue Z = rightEdge.getTarget();
		CfgSymbol label2 = rightEdge.getLabel();
		CfgSymbol label = label1.concat(label2);
		
		if (label == null) return; // otherwise, label is guaranteed to be LOCAL

		assert label == CfgSymbol.LOCAL : "Label is: "+label;

		addLocalEdge(queue, visited, X, Z);
		// System.out.println("Added edge "+X.toString() + "--- "+label.toString()+" --->"+Z.toString());
	}

	// requires: all args except for label are non-null
	protected void addLocalEdge(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Set<Edge<AnnotatedValue, CfgSymbol>> visited,
			AnnotatedValue X, AnnotatedValue Z) {
		
		if (X.equals(Z)) return;
		if (X.getEnclosingMethod() == null || Z.getEnclosingMethod() == null) return;		
		
		
		if (skipAddEdge(X,Z)) return;
		
		
		Edge<AnnotatedValue,CfgSymbol> transitiveEdge = new Edge<AnnotatedValue, CfgSymbol>(X,Z,CfgSymbol.LOCAL);
		if (!visited.contains(transitiveEdge) && !graph.hasEdge(X,Z,CfgSymbol.LOCAL)) {
			visited.add(transitiveEdge);
			queue.add(transitiveEdge);
			addLocal(X,Z,true); // Want to add this only to graph.
			//System.out.println("Added transitive local edge: "+transitiveEdge.toString());
						
		}
	}
	
	// modifies: graph
	// effects: tracks X -(_i-> Y -)_i-> Z edges and adds local edges from X to Z
	//          TO BE CALLED after collapsing nodes into SCC
	public void addLocalEdges() {
		for (AnnotatedValue node : graph.getNodes()) {
			List<Edge<AnnotatedValue,CfgSymbol>> nexts = graph.getEdgesFrom(node);
			List<Edge<AnnotatedValue,CfgSymbol>> prevs = graph.getEdgesInto(node);
			
			for (Edge<AnnotatedValue,CfgSymbol> next : nexts) {
				for (Edge<AnnotatedValue,CfgSymbol> prev : prevs) {
					AnnotatedValue X = prev.getSource();
					AnnotatedValue Z = next.getTarget();
										
					if (prev.getLabel().match(next.getLabel())) {
						
						//if (X.equals(Z)) continue;						
						
						if (!skipAddEdge(X,Z)) {						
							addLocal(X,Z,true); // Want to add only to graph...
							// System.out.println("Added a new local edge: "+X+"---->"+Z);
							// THIS IS A TRANSITIVE EDGE. HAVE TO ADD TO TRANSITIVE EDGES!!!
							transitiveEdges.addEdge(new Edge(X,Z,CfgSymbol.LOCAL));
							//if (UtilFuncs.getNodeRepFromId(12359,nodeToRep).getId() == X.getId()) {
							//	System.out.println("TR EDGE FROM "+X);
							//	System.out.println("TO "+Z);
							//}
						}
					}
				}
			}
		}
		/*
		System.out.println("AFTER COLLAPSE LOCAL EDGES:");
		for (AnnotatedValue n : nodeToRep.keySet()) {				
			if (UtilFuncs.getNodeRepFromId(13829,nodeToRep).getId() == nodeToRep.get(n).getId()) {
				System.out.println("==== Node in equiv class of r1.add: "+n);
			}
		}
		
		for (AnnotatedValue n : nodeToRep.keySet()) {				
			if (UtilFuncs.getNodeRepFromId(13827,nodeToRep).getId() == nodeToRep.get(n).getId()) {
				System.out.println("==== Node in equiv class of $r2.add: "+n);
			}
		}
		*/
		
		
	}
	
	// Code Building the Pt graph
	
	/*
	//BEGIN TRY
	HashMap<AnnotatedValue,HashSet<AnnotatedValue>> tryPtGraph = new HashMap<AnnotatedValue, HashSet<AnnotatedValue>>();
	//END TRY
	*/
	
	/*
	public void buildPtGraph() {
		HashSet<AnnotatedValue> visited = new HashSet<AnnotatedValue>();
		ptGraph = new CompactGraph<AnnotatedValue>();
		
		int total=0;
		for (AnnotatedValue node : nodeToRep.keySet()) {
			//System.out.println("Current node, of kind: "+node+" node kind: "+node.getKind());
			if (node.getKind() == AnnotatedValue.Kind.ALLOC) {
				total++;
			}
		}
		
		int i=0;	
		for (AnnotatedValue node : nodeToRep.keySet()) {
			//System.out.println("Current node, of kind: "+node+" node kind: "+node.getKind());
			if (node.getKind() == AnnotatedValue.Kind.ALLOC) {
				//System.out.println("Current node: "+node);				
				AnnotatedValue n = nodeToRep.get(node);
				if (visited.contains(n)) continue; // already examined.
				visited.add(n);
				 
				System.out.println("Start propagate alloc value "+n+" number "+i++ +" out of"+total+" allocs...");
				
				Queue<Edge<AnnotatedValue,CfgSymbol>> queue = new LinkedList();
				// initialize queue
				for (Edge<AnnotatedValue,CfgSymbol> e : graph.getEdgesFrom(n)) {
					addEdgeToQueue(queue, e);
				}
				// propagate queue
				while (!queue.isEmpty()) {
					Edge<AnnotatedValue,CfgSymbol> e1 = queue.remove();
					for (Edge<AnnotatedValue,CfgSymbol> e2 : graph.getEdgesFrom(e1.getTarget())) {
						addTransitiveEdgeToQueue(queue, e1, e2);
					}
				}
			}
		}
	}
	
	abstract protected void addTransitiveEdgeToQueue(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Edge<AnnotatedValue, CfgSymbol> e1, Edge<AnnotatedValue, CfgSymbol> e2); 
	
	abstract protected void addEdgeToQueue(Queue<Edge<AnnotatedValue, CfgSymbol>> queue, Edge<AnnotatedValue, CfgSymbol> e); 
	
	
	public HashMap<AnnotatedValue,HashSet<AnnotatedValue>> getPtSets(ConstraintGraph field) {
		HashMap<AnnotatedValue,HashSet<AnnotatedValue>> varToPtSet = new HashMap<AnnotatedValue, HashSet<AnnotatedValue>>();
		// Map from each rep to the set of Allocs new_i such that nodeToRep(new_i) = rep; null if no allocs.
		HashMap<AnnotatedValue,HashSet<AnnotatedValue>> repToAllocs = new HashMap<AnnotatedValue, HashSet<AnnotatedValue>>();
		for (AnnotatedValue alloc : nodeToRep.keySet()) {
			if (alloc.getKind() != AnnotatedValue.Kind.ALLOC) continue;
			//System.out.println("Adding "+alloc+" to the rep set for "+nodeToRep.get(alloc));
			addToMap(repToAllocs,nodeToRep.get(alloc),alloc);
			// will add reflexive pt edge for each ALLOC. TODO: maybe remove.
			addToMap(varToPtSet,nodeToRep.get(alloc),alloc);
		}		
		for (AnnotatedValue var : nodeToRep.keySet()) {
			//System.out.println("var is "+var+ " and its rep is "+nodeToRep.get(var));
			for (Edge<AnnotatedValue,CfgSymbol> edge : ptGraph.getEdgesInto(nodeToRep.get(var))) {
				// This is the points to edge
				// System.out.println("----- HERE. Trying to add for "+var);
				for (AnnotatedValue alloc : repToAllocs.get(edge.getSource())) {	
					// System.out.println("----- HERE2. Trying to add for "+var+" the alloc: "+alloc);
					if (typeCompatible(alloc.getType(),var.getType())) {
						//System.out.println("----- Adding for "+var+" the alloc: "+alloc);
						addToMap(varToPtSet,var,alloc);
					}
				}
			}
			// In addition, we have to find all allocs in repToAlloc for the rep, and add them to pt set of var
			// if alloc,x,y,z all map to say x, then y and z aren't found to point to alloc.
			if (repToAllocs.get(nodeToRep.get(var)) != null) {
				for (AnnotatedValue alloc : repToAllocs.get(nodeToRep.get(var))) {
					if (typeCompatible(alloc.getType(),var.getType())) {
						//System.out.println("----- 2 Adding for "+var+" the alloc: "+alloc);
						addToMap(varToPtSet,var,alloc);
					}
				}
			}
		}
		HashMap<AnnotatedValue,HashSet<AnnotatedValue>> ptSets = new HashMap();
		intersectPtSets(field, varToPtSet, ptSets);
		return ptSets;
	}
	
	// effects: intersects this.ptGraph with field.ptGraph and stores result in ptSet hashMap
	private void intersectPtSets(ConstraintGraph field,
			HashMap<AnnotatedValue, HashSet<AnnotatedValue>> varToPtSet, HashMap<AnnotatedValue,HashSet<AnnotatedValue>> ptSets) {
		System.out.println(getClass()+"\n\n\n");
		long numpt = 0;
		long total = 0;
		
		String outputDir = SourceLocator.v().getOutputDir();
		String fileName = getClass().toString().substring(getClass().toString().lastIndexOf(".")+1);
		if (field != null) fileName = fileName+"_intersect"; 
		PrintStream ptDump = null;
		
		try {
			ptDump = new PrintStream(outputDir + File.separator + fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (AnnotatedValue v : varToPtSet.keySet()) {
			//if (v.getType().toString().equals("java.lang.Object")) continue;
			ptDump.println("The pt set for "+v);
			total++;
			for (AnnotatedValue alloc : varToPtSet.get(v)) {
				if (field != null) {
					if ( field.ptGraph.hasEdge(field.nodeToRep.get(alloc), field.nodeToRep.get(v), CfgSymbol.LOCAL) ||
						     (field.nodeToRep.get(alloc) == field.nodeToRep.get(v))) {
						ptDump.println("---- "+alloc);
						numpt++;
						addToMap(ptSets,v,alloc);
					}
				}
				else {
					ptDump.println("---- "+alloc);		
					numpt++;
				}
			}
		}
		double avg = ((double) numpt)/((double) total);
		System.out.println("Avg pt set size: "+avg);
	}
	*/

	
	// ANA: BEGIN TRY, propagation over original graph...

	protected void collectTransitiveSourceAndTargetNodes(HashMap<AnnotatedValue,HashSet<AnnotatedValue>> incomingMap,
			HashMap<AnnotatedValue,HashSet<AnnotatedValue>> outgoingMap) {
		System.out.println("Started collecting in and out maps.");
		for (AnnotatedValue node : originalGraph.getNodes()) {
			for (Edge e : originalGraph.getEdgesFrom(node)) {
				if (e.getLabel() instanceof AtomicOpenParen) {
					AtomicOpenParen label = (AtomicOpenParen) e.getLabel();
					UtilFuncs.addToMap(outgoingMap,node,label.getInfo());
					if (!UtilFuncs.isReadonly(node,reimTransformer)) {
						UtilFuncs.addToMap(incomingMap,node,label.getInfo());
					}
				}
			}
			for (Edge e : originalGraph.getEdgesInto(node)) {
				if (e.getLabel() instanceof AtomicCloseParen) {
					AtomicCloseParen label = (AtomicCloseParen) e.getLabel();
					UtilFuncs.addToMap(incomingMap,node,label.getInfo());
					if (!UtilFuncs.isReadonly(node,reimTransformer)) {
						UtilFuncs.addToMap(outgoingMap,node,label.getInfo());
					}
				}
			}
		}
		System.out.println("Done collecting in and out maps.");
	}
	
	protected Set<AnnotatedValue> intersect(Set<AnnotatedValue> s1, Set<AnnotatedValue> s2) {
		Set<AnnotatedValue> copy = new HashSet<AnnotatedValue>(s1);
		copy.retainAll(s2);
		return copy;
	}
	
	
	public void buildPtGraph() {
				
		
		ptGraph = new CompactGraph<AnnotatedValue>();
		
		//System.out.println("transitiveEdges has "+transitiveEdges.size()+ " edges! ");
		/*
		HashMap<AnnotatedValue,HashSet<AnnotatedValue>> revNodeToRep = new HashMap<AnnotatedValue,HashSet<AnnotatedValue>>();
		for (AnnotatedValue X : nodeToRep.keySet()) {
			//System.out.println("Adding "+X+" to "+nodeToRep.get(X));
			addToMap(revNodeToRep,nodeToRep.get(X),X);
		}
		*/
		
		// Needed just for information
		int total=0;
		for (AnnotatedValue node : nodeToRep.keySet()) {
			//System.out.println("Current node, of kind: "+node+" node kind: "+node.getKind());
			if (node.getKind() == AnnotatedValue.Kind.ALLOC) {
				total++;
			}
		}
		
		
		int i=0;
		for (AnnotatedValue node : nodeToRep.keySet()) {
			//System.out.println("Current node, of kind: "+node+" node kind: "+node.getKind());
			if (node.getKind() == AnnotatedValue.Kind.ALLOC) {
				
				HashSet<AnnotatedValue> fieldWriteNodes = new HashSet<AnnotatedValue>();
				
				System.out.println("Start propagate alloc value "+node+" number "+i++ +" out of"+total+" allocs...");
				
				Queue<Edge<AnnotatedValue,CfgSymbol>> queue = new LinkedList();
				// initialize queue
				for (Edge<AnnotatedValue,CfgSymbol> e : originalGraph.getEdgesFrom(node)) {
					// System.out.println("HERE for "+node);
					addEdgeToQueue(queue, e);
				}
				// propagate queue
				
				int queueSize = 0;
				
				while (!queue.isEmpty()) {
					
					// if (node.getId() == 29111 && queueSize++ >= 20) return; // ANA: REMOVE THIS!!!!
					
					Edge<AnnotatedValue,CfgSymbol> e1 = queue.remove();
					
					if (node.getId() == 29111) { 
						System.out.println("--- TOOK OFF EDGE OFF QUEUE: "+e1.getTarget());												
					}
					// if (node.getId() == 29111) System.out.println("--- TOOK OFF EDGE OFF QUEUE: "+e1);
					
					for (Edge<AnnotatedValue,CfgSymbol> e2 : originalGraph.getEdgesFrom(e1.getTarget())) {
						if (node.getId() == 29111)  System.out.println("-------- AND ORIG e2: "+e2);
						
						if (fieldWriteNodes.contains(e1.getTarget()) || isFieldWrite(e2) ) {
							if (addTransitiveEdgeToQueue(queue, e1, e2)) {
								fieldWriteNodes.add(e2.getTarget());
							}
						}
						else {
							addTransitiveEdgeToQueue(queue, e1, e2);
						}
					}
					
					for (Edge<AnnotatedValue,CfgSymbol> e2 : transitiveEdges.getEdgesFrom(e1.getTarget())) {
						if (node.getId() == 29111) System.out.println("-------- AND e2: "+e2);
						if (fieldWriteNodes.contains(e1.getTarget()) || isFieldWrite(e2) ) {
							if (addTransitiveEdgeToQueue(queue, e1, e2)) {
								fieldWriteNodes.add(e2.getTarget());
							}
						}
						else {
							addTransitiveEdgeToQueue(queue, e1, e2);
						}
					}
					
					//if (e1.getSource().getId() == 28944 && e1.getTarget().getId() == 28941) System.out.println("**** HERE");
					
					if (fieldWriteNodes.contains(e1.getTarget())) {
						//if (e1.getSource().getId() == 28944 && e1.getTarget().getId() == 28941) System.out.println("-------- AND INVERSES for e1...");
						for (Edge<AnnotatedValue,CfgSymbol> e2 : originalGraph.getEdgesInto(e1.getTarget())) {
							if (e1.getSource().getId() == 29111) System.out.println("------- HERE: "+e2);
							CfgSymbol label = null;
							if (e2.getSource().getKind() == AnnotatedValue.Kind.ALLOC) continue; // No need to add self-edges for ALLOCs
							if (e2.getLabel() == CfgSymbol.OPENPAREN && 
									graph.hasEdge(nodeToRep.get(e2.getTarget()),nodeToRep.get(e2.getSource()),CfgSymbol.CLOSEPAREN)) {
								label = CfgSymbol.CLOSEPAREN;
							}
							else if (e2.getLabel() == CfgSymbol.LOCAL &&
									(graph.hasEdge(nodeToRep.get(e2.getTarget()),nodeToRep.get(e2.getSource()),CfgSymbol.LOCAL) || 
											nodeToRep.get(e2.getTarget()) == nodeToRep.get(e2.getSource()))) {
								label = CfgSymbol.LOCAL;
							}
							else {
								continue;
							}
							// if (e1.getSource().getId() == 28944 && e1.getTarget().getId() == 28941) System.out.println("------- HERE2: "+e2);
							
							if (addTransitiveEdgeToQueue(queue, e1, new Edge(e2.getTarget(),e2.getSource(),label))) {
								fieldWriteNodes.add(e2.getSource());
							}
						}
					}
					
					
				}
			}						
		}
	}
	
	abstract protected boolean addTransitiveEdgeToQueue(Queue<Edge<AnnotatedValue, CfgSymbol>> queue,
			Edge<AnnotatedValue, CfgSymbol> e1, Edge<AnnotatedValue, CfgSymbol> e2); 
	
	abstract protected void addEdgeToQueue(Queue<Edge<AnnotatedValue, CfgSymbol>> queue, Edge<AnnotatedValue, CfgSymbol> e); 
	
	abstract protected boolean isFieldWrite(Edge<AnnotatedValue, CfgSymbol> e);
	
	
	public HashMap<AnnotatedValue,HashSet<AnnotatedValue>> getPtSets(ConstraintGraph field) {
		HashMap<AnnotatedValue,HashSet<AnnotatedValue>> varToPtSet = new HashMap<AnnotatedValue, HashSet<AnnotatedValue>>();
				
		for (AnnotatedValue var : nodeToRep.keySet()) {
			//System.out.println("var is "+var+ " and its rep is "+nodeToRep.get(var));
			for (Edge<AnnotatedValue,CfgSymbol> edge : ptGraph.getEdgesInto(var)) {
				// This is the points to edge
				// System.out.println("----- HERE. Trying to add for "+var);
				if (UtilFuncs.typeCompatible(edge.getSource().getType(),var.getType())) {
						//System.out.println("----- Adding for "+var+" the alloc: "+alloc);
						UtilFuncs.addToMap(varToPtSet,var,edge.getSource());
					}
			}
		}

		HashMap<AnnotatedValue,HashSet<AnnotatedValue>> ptSets = new HashMap();
		intersectPtSets(field, varToPtSet, ptSets);
		return ptSets;
	}
	
	// effects: intersects this.ptGraph with field.ptGraph and stores result in ptSet hashMap
	private void intersectPtSets(ConstraintGraph field,
			HashMap<AnnotatedValue, HashSet<AnnotatedValue>> varToPtSet, HashMap<AnnotatedValue,HashSet<AnnotatedValue>> ptSets) {
		System.out.println(getClass()+"\n\n\n");
		long numpt = 0;
		long total = 0;
		
		String outputDir = SourceLocator.v().getOutputDir();
		String fileName = getClass().toString().substring(getClass().toString().lastIndexOf(".")+1);
		if (field != null) fileName = fileName+"_intersect"; 
		PrintStream ptDump = null;
		
		try {
			ptDump = new PrintStream(outputDir + File.separator + fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (AnnotatedValue v : varToPtSet.keySet()) {
			//if (v.getType().toString().equals("java.lang.Object")) continue;
			ptDump.println("The pt set for "+v);
			total++;
			for (AnnotatedValue alloc : varToPtSet.get(v)) {
				if (field != null) {
					if ( field.ptGraph.hasEdge(alloc, v, CfgSymbol.LOCAL) ) {
						ptDump.println("---- "+alloc);
						numpt++;
						UtilFuncs.addToMap(ptSets,v,alloc);
					}
				}
				else {
					ptDump.println("---- "+alloc);		
					numpt++;
					UtilFuncs.addToMap(ptSets,v,alloc);
				}
			}
		}
		double avg = ((double) numpt)/((double) total);
		System.out.println("Avg pt set size: "+avg);
	}
	
	// ANA: END TRY, propagation over original graph!!!
		
}
