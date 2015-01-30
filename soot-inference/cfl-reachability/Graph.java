import java.util.*;
import java.io.*;

/* Node is an immutable class */

class Node {
    long id;
    String name;
    String sflowType;
    String javaType;
    String enclMethod;
    String enclClass;
    String kind; // one of "local", "field", "static field", "CLASS", "lib", "array access"
    boolean isTainted; // when kind == "field" {Tainted,Poly}, otherwise == {Tainted,Poly,Safe}

    public boolean equals(Object n) {
	Node other = (Node) n;
	if (id == other.id) 
	    return true;
	else
	    return false;
    }
    public int hashCode() {
	return (int) id;
    }
    void printNode() {
	System.out.println("[Node Id: "+id+"], [Java Type & Name: "+javaType+" "+name+"], [EnclClass: "+enclClass+"], [EnclMethod: "+enclMethod+
                            "], [SflowType: "+sflowType+"], [Kind: "+kind+"]");
    }

    boolean isType(String type) {
	if (sflowType.indexOf(type) > -1) return true;
	return false;
    }

    boolean isSink() {
	if (!kind.equals("lib")) return false;
	if (isType("@Poly") || isType("@Tainted")) return false;
	return true;
    }
    boolean isParameter() {
	if ((name.indexOf("parameter")>-1) || name.equals("@this")) return true;
	return false;
    }
    boolean isReturn() {
	if (name.indexOf("@return")>-1) return true;
	return false;
    }

}
/* TODO: Make Edge an abstract superclass with all 
   kinds of Edges subclasses */
/* Edge is an immutable class */

class Edge {
    long id;
    long source;
    long target;
    long field;
    long call;
    String info; // One of "local", "call", "call-super", 
                 // "return", "return-super", 
                 // "write", "read", "toStatic", "fromStatic", 
                 // "subtype-minus" (contravariant subtyping), "subtype-plus" (covariant subtyping), 
                 // "lib-lib", "toCLASS", "fromCLASS"
    
    Edge(long id, long s, long t, long c, long f, String i) {
	this.id = id;
	this.source = s;
	this.target = t;
	this.field = f;
	this.call = c;
	this.info = i;
    }
    public boolean equals(Object e) {
	Edge other = (Edge) e;
	if ((source == other.source) && (target == other.target) && 
            (field == other.field) && (call == other.call) && (info.equals(other.info)))
	    return true;
	else
	    return false;		
    }
    public int hashCode() {
	long l = source+target+field+call;
	return (int) l;
    }

    public boolean isInverseOf(Edge e) {
	if ((source == e.target) && (target == e.source) && (call == -e.call) && (field == -e.field)) return true;
	else return false;
    }

    void printEdge() {
        System.out.println("[Edge Id: "+id+"], [Source: "+source+"], [Target: "+target+"], [Field: "+field+"], [Call: "+call+
                           "], [Info: "+info+"]");
    }


}

 /* Class Side records the call, field, and var id of lhs or rhs 
    of the constraint. Used in liue of out parameters.
 */
class Side {
    long call;
    long field;
    long var;
}

public class Graph {

    Hashtable<Long,Node> nodes = new Hashtable<Long,Node>(); // Node id -> Node
    Hashtable<Long,Edge> edges = new Hashtable<Long,Edge>(); // Edge id -> Edge
    Hashtable<Long,HashSet<Edge>> adjLists = new Hashtable<Long,HashSet<Edge>>(); 
    // Source node id -> HashSet of Edges with source node id


    HashSet<String> reachableMethods = new HashSet<String>(); // the reachable methods

    void registerReachableMethod(String method) {
	reachableMethods.add(method);
    }


    Node getNode(long n) {
	return nodes.get(new Long(n));
    }

    long registerNode(String side, boolean isField) {
	// System.out.println("------------> Registering node: "+side);
	long id;
	if (isField) id = Parser.ARRAY_ACCESS + Long.parseLong(getSubstring(side,'(',')'));
	else id = Long.parseLong(getSubstring(side,'(',')'));
	if (getNode(id) == null) { // need to parse and add node into Hashtable of nodes
	    Node node = new Node();
	    node.id = id;
	    if ((side.indexOf("@CLASS") > -1) || (side.indexOf("this$0") > 0)) {
		// CLASS and inner class this$0 should be treated the same
		node.enclClass = ""; 
		node.enclMethod = "";
		node.name = getSubstring(side,')',':');
		node.kind = "CLASS";
	    } 
	    else {		
		node.name = getSubstring(side,'>',':');	
		if (node.name.equals("")) {
		    if (isField == true) // is instance field!
			node.kind = "field";
		    else  // is static field!
			node.kind = "static field";		
		    node.name = getSubstring(side,':','>'); node.enclMethod = ""; 
		}
		else { // it is a variable or an array field.
		    if (side.indexOf("lib-<") > -1) node.kind = "lib";
		    else node.kind = "local";
		    node.enclMethod = getSubstring(side,':','>');
		    if (isField) { node.kind = "array access"; } // System.out.print("A FIELD node: "); node.printNode(); }
		}
		node.enclClass = getSubstring(side,'<',':');
	    }

	    node.sflowType = getSubstring(side, '{', '}');
	    node.javaType = getJavaType(side); 
	    // System.out.print("=========> The new node: ");
	    // node.printNode();

	    node.isTainted = node.kind.equals("field") ? (node.sflowType.indexOf("@Tainted") > -1) : (node.sflowType.indexOf("@Tainted") > -1) && (node.sflowType.indexOf("@Poly") > -1) && (node.sflowType.indexOf("@Safe") > -1);
   
	    nodes.put(new Long(id),node);

	}
	return id;
    }

    Edge lastRegisteredEdge;

    void registerEdge(long edgeId, long s, long t, long call, long field, String libMethod) {
	Node rhs = getNode(t);
	Node lhs = getNode(s);
	Node fld = getNode(Math.abs(field));
	if (rhs.isTainted) return; // No need to record the edge if rhs is Tainted. Brings no info.
	if (lhs.isTainted) return; // Same as above.
	if ((field < 0) && (fld != null) && fld.isTainted) return; // Same for field write into Tainted field.
	// fld is null when array access. TODO. Needs a fix: what if [] field is Tainted?

	//if ((lhs.name.indexOf("@CLASS")>-1) && ((rhs.enclMethod.indexOf("void on")<0) && (rhs.enclMethod.indexOf("void <init>")<0))) return;
	//if ((rhs.name.indexOf("@CLASS")>-1) && ((lhs.enclMethod.indexOf("void on")<0) && (lhs.enclMethod.indexOf("void <init>")<0))) return;

	Edge edge = new Edge(edgeId,s,t,call,field,null);
	if (call < 0) { 
	    /* A hack. Since I don't have info on specialinvokes, I glean some of them by checking lhs and rhs */
	    if (lhs.name.equals("@$r0") && rhs.name.equals("@this") && lhs.enclMethod.equals(rhs.enclMethod) && 
                !lhs.enclClass.equals(rhs.enclClass))
		edge.info = "call-super";
	    else 
		edge.info = "call";
	}
	else if (call > 0) {
	    if (lhs.name.equals("@this") && rhs.name.equals("@$r0") && lhs.enclMethod.equals(rhs.enclMethod) &&
                !lhs.enclClass.equals(rhs.enclClass))
                edge.info = "return-super";
            else
		edge.info = "return";
	}
	else if (field < 0) edge.info = "write";
	else if (field > 0) edge.info = "read";
	else if (rhs.kind.equals("static field")) edge.info = "toStatic";
	else if (lhs.kind.equals("static field")) edge.info = "fromStatic";
	else if (lhs.enclMethod.equals(rhs.enclMethod)) {
	    if (lhs.enclClass.equals(rhs.enclClass)) {
		if (lhs.kind.equals("lib") && rhs.kind.equals("lib")) edge.info = "lib-lib";
		else edge.info = "local"+libMethod;
	    }
	    else { 
		if (lhs.isParameter()) {
		    if ((lastRegisteredEdge == null) || !lastRegisteredEdge.isInverseOf(edge)) edge.info = "subtype-minus";
		    else edge.info = "subtype-plus";
		}
		else if (lhs.isReturn()) {
		    if ((lastRegisteredEdge == null) || !lastRegisteredEdge.isInverseOf(edge)) edge.info = "subtype-plus";
                    else edge.info = "subtype-minus";
		}
		else 
		    { edge.info = "other"; // System.out.println("Here 2."); lhs.printNode(); rhs.printNode(); return; 
                      /* System.out.print("HERE BUT IT SHOULDN'T. The edge: "); lhs.printNode(); edge.printEdge(); */ }
		// edge.info = "subtype";
	    }
	}
	else if (lhs.kind.equals("CLASS")) 
	    edge.info = "fromClass";
	else if (rhs.kind.equals("CLASS"))
	    edge.info = "toClass";	
	else if (lhs.enclMethod.indexOf("access$") > -1) { 
	    edge.call = getNextFakeCall(lhs.enclMethod+rhs.enclClass+rhs.enclMethod);
	    edge.info = "return";
	    //edge.info = "other-return";
	    
	}
	else if (rhs.enclMethod.indexOf("access$") > -1) {
	    edge.call = -getNextFakeCall(rhs.enclMethod+lhs.enclClass+lhs.enclMethod);
	    edge.info = "call";
	    //edge.info = "other-call";
	}
	else { edge.info = "other"; // System.out.println("Here 3."); lhs.printNode(); rhs.printNode(); 
               return; }
	// other are equality edges Wei creates to handle field reads and writes.
	// we can avoid recording the edge

	// System.out.print("------->Registering Edge: "); edge.printEdge();
	registerEdgeIntoAdjList(s,edge);

	edges.put(new Long(edgeId),edge);
	lastRegisteredEdge = edge;
    } 
   

    /* calls to access$xyz methods --- they access fields of enclosing class from
       inner class --- are not marked as calls and returns. We track those calls 
       and mark them using a fake callsite number starting at FAKE_CALL */
    Hashtable<String,Long> fakecalls = new Hashtable<String,Long>();
    long getNextFakeCall(String callerCallee) {
	Long l = fakecalls.get(callerCallee);
	if (l == null) {
	    long size = fakecalls.size();
	    l = Parser.FAKE_CALL + size;
	    fakecalls.put(callerCallee,l);
	}
	return l.longValue();
    }


    void registerCausedByEdge(long edgeId, long lhs, long rhs, long call, long field, String causedBy) {
	// Looking for l -(_id-> lib1 -> lib2 -)_id-> l2 edge.

	int firstComma = causedBy.indexOf(','); if (firstComma < -1) return;
	int secondComma = causedBy.lastIndexOf(','); if (secondComma == firstComma) return;
	// System.out.println("CausedBy string: "+causedBy);
	long callConstraint = Long.parseLong(causedBy.substring(1,firstComma));
	long liblib = Long.parseLong(causedBy.substring(firstComma+1,secondComma));
	long retConstraint = Long.parseLong(causedBy.substring(secondComma+1,causedBy.length()-1));
	Edge callEdge = edges.get(new Long(callConstraint));
	Edge liblibEdge = edges.get(new Long(liblib));
	Edge retEdge = edges.get(new Long(retConstraint));
	if ((callEdge == null) || (liblibEdge == null) || (retEdge == null)) return;
	if (!liblibEdge.info.equals("lib-lib")) return;
	if ((callEdge.info.equals("call") || callEdge.info.equals("call-super")) && 
            (retEdge.info.equals("return") || retEdge.info.equals("return-super")) && (callEdge.call == -retEdge.call)) {
	    // System.out.println("About to register a caused-by edge; Must be local");
	    assert(callEdge.target == liblibEdge.source); assert(liblibEdge.target == retEdge.source);
	    String libMethod = getNode(liblibEdge.source).enclMethod;
	    registerEdge(edgeId,lhs,rhs,call,field,libMethod);
	}
    }

    void registerEdgeIntoAdjList(long s, Edge e) {
	Long source = new Long(s);
	HashSet<Edge> set = adjLists.get(source);
	if (set == null) { set = new HashSet<Edge>(); adjLists.put(source,set); }
	set.add(e);
    }

    String getSubstring(String str, char from, char to) {
	int i, j;
	if (from == '>') i = str.lastIndexOf(from)+1;
	else i = str.indexOf(from)+1;
	if (to == '>') j = str.lastIndexOf(to);
	else j = str.indexOf(to,i);
	return str.substring(i,j);
    }

    String getJavaType(String str) {
	int end = str.lastIndexOf(']');
	int start = str.lastIndexOf(' ',end)+2;
	return str.substring(start,end);
    }


    void bfs(Node source) {
	
	HashSet<ExtendedVertex> set = new HashSet<ExtendedVertex>();
	LinkedList<ExtendedVertex> queue = new LinkedList<ExtendedVertex>();
	ExtendedVertex s = new ExtendedVertex(source.id,new ArrayList<Long>(),false,new ArrayList<Long>(),false);
	s.depth = 0;
	s.printVertex(0);
	s.parent = null;
	set.add(s); queue.add(s);
	HashSet<Node> alreadyIn = new HashSet<Node>();

	while (queue.size() > 0) {
	    ExtendedVertex v = queue.remove();
	    // System.out.print("\nProcessing dequed node at detph: "+v.depth+" "); nodes.get(new Long(v.id)).printNode();
	    // v.printVertex(0);
	    HashSet<Edge> edges = adjLists.get(new Long(v.id));
	    if (edges == null) continue;

	    if (v.depth > 100) continue;

	    //Checks if v is in a reachable method
	    Node tmp = nodes.get(new Long(v.id));	    
	    if (tmp.kind.equals("local") && !reachableMethods.contains(tmp.enclClass+":"+tmp.enclMethod)) {
		System.out.println(tmp.enclClass+":"+tmp.enclMethod+" is not reachable!");
		tmp.printNode();
		continue;
	    }


	    for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
		Edge edge = it.next(); // current edge to examine.
		// System.out.print("Target of edge: "); nodes.get(new Long(edge.target)).printNode();
		
		if (getNode(edge.target).isSink()) {
		    if (!alreadyIn.contains(getNode(edge.target))) {
			alreadyIn.add(getNode(edge.target));
			System.out.print("\n\nFound a good path to sink at depth "+(v.depth+1)+" ");
			getNode(edge.target).printNode();
			retrievePath(v);
			
			try {
			    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			    String command;
			    System.out.println("\nPress Enter to search for more paths, or type \"no\" to exit"); command = in.readLine();
			    if (command.equals("no")) {
				queue = new LinkedList<ExtendedVertex>(); // empties the queue so outer loop exits			       
			    }
			}
			catch (IOException e) { }


		    }
                    continue;
		}
		else if (getNode(edge.target).kind.equals("lib")) continue;

		ExtendedVertex t = v.newExtendedVertex(edge);
		if (t == null) { /* System.out.print("t is null for edge "); edge.printEdge(); */  continue; }

		if (!set.contains(t)) {
		    set.add(t);
		    queue.add(t);
		    // System.out.print("\n----Added node to set and queue at depth: "+t.depth+" "); nodes.get(new Long(t.id)).printNode();
		    // t.printVertex(0);
		}
		else {
		    /*System.out.print("target already in for edge "); edge.printEdge(); */
		}

	    }
	}
	System.out.println("\n BFS is DONE!");
    }

    void retrievePath(ExtendedVertex v) {
	ArrayList<ExtendedVertex> list = new ArrayList<ExtendedVertex>();
	ExtendedVertex current = v;
	int currentDepth = 0;
        while (current != null) {
            for(int i=0; i<currentDepth; i++) System.out.print(" ");
	    getNode(current.id).printNode();
	    for(int i=0; i<currentDepth; i++) System.out.print(" ");
	    //list.add(current);
	    current.printVertex(currentDepth);
	    current = current.parent;
	    //currentDepth += 2;
        }
    }

    public static void main(String[] arg) throws IOException {
	String file = arg[0];
	Graph g = new Graph();
	Parser p = new Parser(g);
	p.loadGraph(file);
	
	System.out.println("GRAPH LOADED");

	p.loadReachableMethods(file);

	System.out.println("Reachable Methods LOADED");

	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	String s;

	while (true) {
	    System.out.print("\nEnter the id of the source or press ^C to exit: ");
            s = in.readLine();
	    try {		
		g.bfs(g.nodes.get(new Long(Long.parseLong(s))));
	    }
	    catch (NumberFormatException e) { }
	}

	// System.out.println("# edges: "+g.edges.size());
	// g.bfs(g.nodes.get(new Long(514818))); // uber
	// g.bfs(g.nodes.get(new Long(534752))); // uber-non FP
	// g.bfs(g.nodes.get(new Long(534581))); // uber-non FP
	// g.bfs(g.nodes.get(new Long(534574))); // uber-non FP 
	// g.bfs(g.nodes.get(new Long(195431))); // uber FP
	// g.bfs(g.nodes.get(new Long(426174))); // uber FP
	// g.bfs(g.nodes.get(new Long(787503))); // NYtimes FP
	// g.bfs(g.nodes.get(new Long(117538))); // NYtimes non-FP?
	// g.bfs(g.nodes.get(new Long(399212))); // NYtimes FP

	// g.bfs(g.nodes.get(new Long(53))); // Button1
	// g.bfs(g.nodes.get(new Long(117))); // Button2
	// g.bfs(g.nodes.get(new Long(36))); // Leak1
	// g.bfs(g.nodes.get(new Long(84))); // Leak2
	// g.bfs(g.nodes.get(new Long(12))); // Leak3
    }



}