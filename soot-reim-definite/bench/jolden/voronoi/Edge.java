package voronoi;
import java.io.*;
import java.util.Stack;
import java.util.Hashtable;

/**
 * A class that represents the quad edge data structure which implements
 * the edge algebra as described in the algorithm.
 * <p>
 * Each edge contains 4 parts, or other edges.  Any edge in the group may
 * be accessed using a series of rotate and flip operations.  The 0th
 * edge is the canonical representative of the group.
 * <p>
 * The quad edge class does not contain separate information for vertice
 * or faces; a vertex is implicitly defined as a ring of edges (the ring
 * is created using the next field).
 **/
public class Edge 
{  
  /**
   * Group of edges that describe the quad edge
   **/
  Edge quadList[];
  /**
   * The position of this edge within the quad list
   **/
  int     listPos;
  /**
   * The vertex that this edge represents
   **/
  Vertex  vertex;
  /**
   * Contains a reference to a connected quad edge
   **/
  Edge    next;

  /**
   * Create a new edge which.
   **/
  public Edge(Vertex v, Edge ql[], int pos)
  {
    vertex = v;
    quadList = ql;
    listPos = pos;
  }

  /**
   * Create a new edge which.
   **/
  public Edge(Edge ql[], int pos)
  {
    this(null, ql, pos);
  }

  /**
   * Create a string representation of the edge
   **/
  public String toString()
  {
    if (vertex != null)
      return vertex.toString();
    else 
      return "None";
  }

  public static Edge makeEdge(Vertex o, Vertex d)
  {
    Edge ql[] = new Edge[4];
    ql[0] = new Edge(ql, 0);
    ql[1] = new Edge(ql, 1);
    ql[2] = new Edge(ql, 2);
    ql[3] = new Edge(ql, 3);

    ql[0].next = ql[0];
    ql[1].next = ql[3];
    ql[2].next = ql[2];
    ql[3].next = ql[1];
    
    Edge base = ql[0];
    base.setOrig(o);
    base.setDest(d);
    return base;
  }

  public void setNext(Edge n)
  {
    next = n;
  }

  /**
   * Initialize the data (vertex) for the edge's origin 
   **/
  public void setOrig(Vertex o)
  {
    vertex = o;
  }

  /**
   * Initialize the data (vertex) for the edge's destination
   **/
  public void setDest(Vertex d) 
  {
    symmetric().setOrig(d);
  }
  
  public Edge oNext() 
  {
    return next;
  }

  public Edge oPrev() 
  {
    return this.rotate().oNext().rotate();
  }
 
  public Edge lNext() 
  {
    return this.rotateInv().oNext().rotate();
  }

  public Edge lPrev() 
  {
    return this.oNext().symmetric(); 
  }

  public Edge rNext() 
  {
    return this.rotate().oNext().rotateInv();
  }

  public Edge rPrev()
  {
    return this.symmetric().oNext(); 
  }

  public Edge dNext() 
  {
    return this.symmetric().oNext().symmetric();
  }

  public Edge dPrev()
  {
     return this.rotateInv().oNext().rotateInv();    
  }
 
  public Vertex orig()
  {
    return vertex;
  }  

  public Vertex dest()
  {
    return symmetric().orig();
  }

  /**
   * Return the symmetric of the edge.  The symmetric is the same edge
   * with the opposite direction.
   * @return the symmetric of the edge
   **/
  public Edge symmetric()
  {
    return quadList[(listPos+2)%4];
  }

  /**
   * Return the rotated version of the edge.  The rotated version is a
   * 90 degree counterclockwise turn.
   * @return the rotated version of the edge
   **/
  public Edge rotate()
  {
    return quadList[(listPos+1)%4];
  }

  /**
   * Return the inverse rotated version of the edge.  The inverse 
   * is a 90 degree clockwise turn.
   * @return the inverse rotated edge.
   **/
  public Edge rotateInv()
  {
    return quadList[(listPos+3)%4];
  }
  
  public Edge nextQuadEdge()
  {
    return quadList[(listPos+1)%4];
  }

  public Edge connectLeft(Edge b)
  {
     Vertex t1,t2;
     Edge ans, lnexta;

     t1 = dest();
     lnexta = lNext();
     t2 = b.orig();
     ans = Edge.makeEdge(t1, t2);
     ans.splice(lnexta);
     ans.symmetric().splice(b);
     return ans;
  }

  public Edge connectRight(Edge b)
  {
     Vertex t1,t2;
     Edge ans, oprevb,q1;
  
     t1 = dest();
     t2 = b.orig();
     oprevb = b.oPrev();
     
     ans = Edge.makeEdge(t1, t2);
     ans.splice(symmetric());
     ans.symmetric().splice(oprevb);
     return ans;
  }

  /****************************************************************/
  /*	Quad-edge manipulation primitives
  ****************************************************************/
  public void swapedge()
  {
    Edge a = oPrev();
    Edge syme = symmetric();
    Edge b = syme.oPrev(); 
    splice(a);
    syme.splice(b);
    Edge lnexttmp = a.lNext();
    splice(lnexttmp);
    lnexttmp = b.lNext();
    syme.splice(lnexttmp);
    Vertex a1 = a.dest();
    Vertex b1 = b.dest();
    setOrig(a1);
    setDest(b1); 
  }

  public void splice(Edge b)
  {
    Edge alpha = oNext().rotate();
    Edge beta = b.oNext().rotate();
    Edge t1 = beta.oNext();
    Edge temp = alpha.oNext();
    alpha.setNext(t1);  
    beta.setNext(temp);
    temp = oNext(); 
    t1 = b.oNext(); 
    b.setNext(temp); 
    setNext(t1);
  }
  
  public boolean valid(Edge basel)
  {
    Vertex t1 = basel.orig();
    Vertex t3 = basel.dest();
    Vertex t2 = dest();
    return t1.ccw(t2, t3);
  }

  public void deleteEdge()
  {
    Edge f = oPrev();
    splice(f);
    f = symmetric().oPrev();
    symmetric().splice(f);
  }   

  public static EdgePair doMerge(Edge ldo, Edge ldi, Edge rdi, Edge rdo)
  {
    while (true) {
      Vertex t3 = rdi.orig();
      Vertex t1 = ldi.orig();
      Vertex t2 = ldi.dest();
    
      while (t1.ccw(t2, t3)) {
	ldi = ldi.lNext();
	  
	t1=ldi.orig();
	t2=ldi.dest();
      }
      
      t2=rdi.dest();
    
      if (t2.ccw(t3, t1)) {  
	rdi = rdi.rPrev(); 
      } else
	break; 
    }
  
    Edge basel = rdi.symmetric().connectLeft(ldi);

    Edge lcand = basel.rPrev();
    Edge rcand = basel.oPrev();
    Vertex t1 = basel.orig();
    Vertex t2 = basel.dest();
  
    if (t1 == rdo.orig()) 
      rdo = basel;
    if (t2 == ldo.orig()) 
      ldo = basel.symmetric();
    
    while (true) {
      Edge t = lcand.oNext();
      if (t.valid(basel)) {
	Vertex v4 = basel.orig();
	
	Vertex v1 = lcand.dest();
	Vertex v3 = lcand.orig();
	Vertex v2 = t.dest();
	while (v1.incircle(v2,v3,v4)){
	  lcand.deleteEdge();
	  lcand = t;

	  t = lcand.oNext();
	  v1 = lcand.dest();
	  v3 = lcand.orig();
	  v2 = t.dest();
	}
      }
    
      t = rcand.oPrev();
      if (t.valid(basel)) {
	Vertex v4 = basel.dest();
	Vertex v1 = t.dest();
	Vertex v2 = rcand.dest();
	Vertex v3 = rcand.orig();
	while (v1.incircle(v2,v3,v4)) {
	  rcand.deleteEdge();
	  rcand = t;
	  t = rcand.oPrev();
	  v2 = rcand.dest();
	  v3 = rcand.orig();
	  v1 = t.dest();
	}
      }
      
      boolean lvalid = lcand.valid(basel);
    
      boolean rvalid = rcand.valid(basel);
      if ((!lvalid) && (!rvalid)) {
	return new EdgePair(ldo, rdo);
      }

      Vertex v1 = lcand.dest();
      Vertex v2 = lcand.orig();
      Vertex v3 = rcand.orig();
      Vertex v4 = rcand.dest();
      if (!lvalid || (rvalid && v1.incircle(v2,v3,v4))) {
	basel = rcand.connectLeft(basel.symmetric());
	rcand = basel.symmetric().lNext();
      } else {
	basel = lcand.connectRight(basel).symmetric();
	lcand = basel.rPrev();
      }
    }
  }


  /**
   * Print the voronoi diagram and its dual, the delaunay triangle for the
   * diagram.
   **/
  public void outputVoronoiDiagram()
  {
    Edge nex = this;
    //  Plot voronoi diagram edges with one endpoint at infinity.
    do {
      Vec2 v21 = (Vec2)nex.dest();
      Vec2 v22 = (Vec2)nex.orig();
      Edge tmp = nex.oNext();
      Vec2 v23 = (Vec2)tmp.dest();
      Vec2 cvxvec = v21.sub(v22);
      Vec2 center = v22.circle_center(v21, v23);
	
      Vec2 vv1 = v22.sum(v22);
      Vec2 vv2 = vv1.times(0.5);
      Vec2 vv3 = center.sub(vv2);
      double ln = 1.0 + vv3.magn();
      double d1 = ln/cvxvec.magn();
      vv1 = cvxvec.cross();
      vv2 = vv1.times(d1) ;
      vv3 = center.sum(vv2);
      System.out.println("Vedge " + center.toString() + " " + vv3.toString());
      nex = nex.rNext();
    } while (nex != this);
  
    // plot delaunay triangle edges and finite VD edges.
    Stack edges = new Stack();
    Hashtable seen = new Hashtable();
    pushRing(edges, seen);
    System.out.println("no. of edges = " + edges.size());
    while (!edges.empty()) {
      Edge edge = (Edge)edges.pop();
      Boolean b = (Boolean)seen.get(edge);
      if (b != null && b.booleanValue()) {
	Edge prev = edge;
	nex = edge.oNext();
	do {
	  Vertex v1 = prev.orig();
	  double d1 = v1.X();
	  Vertex v2 = prev.dest();
	  double d2 = v2.X();
	  if (d1 >= d2) {
	    System.out.println("Dedge " + v1 + " " + v2);
	    Edge sprev = prev.symmetric();
	    Edge snex = sprev.oNext();
	    v1 = prev.orig();
	    v2 = prev.dest();
	    Vertex v3 = nex.dest();
	    Vertex v4 = snex.dest();
	    if (v1.ccw(v2, v3) != v1.ccw(v2, v4)) {
	      Vec2 v21 = prev.orig();
	      Vec2 v22 = prev.dest();
	      Vec2 v23 = nex.dest();
	      Vec2 vv1 = v21.circle_center(v22, v23);
	      v21 = sprev.orig();
	      v22 = sprev.dest();
	      v23 = snex.dest();
	      Vec2 vv2 = v21.circle_center(v22, v23);
	      System.out.println("Vedge " + vv1.toString() + " " + vv2.toString());
	    }
	  }
	  seen.put(prev, new Boolean(false));
	  prev = nex;
	  nex = nex.oNext();
	} while (prev != edge);
      }
      edge.symmetric().pushRing(edges, seen);
    }
  }

  public void pushRing(Stack stack, Hashtable seen)
  {
    Edge nex = oNext();
    while (nex != this) {
      if (!seen.containsKey(nex)) {
	seen.put(nex, new Boolean(true));
	stack.push(nex);
      }
      nex = nex.oNext();
    }
  }

  public void pushNonezeroRing(Stack stack, Hashtable seen)
  {
    Edge nex = oNext();
    while (nex != this) {
      if (seen.containsKey(nex)) {
	seen.remove(nex);
	stack.push(nex);
      }
      nex = nex.oNext();
    }
  }

}

