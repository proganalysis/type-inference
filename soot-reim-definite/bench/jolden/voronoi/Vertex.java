package voronoi;
import java.io.*;

/**
 * A class that represents a voronoi diagram.  The diagram is represnted
 * as a binary tree of points.
 **/
public class Vertex extends Vec2
{
  /**
   * The left child of the tree that represents the voronoi diagram.
   **/
  Vertex left;
  /**
   * The right child of the tree that represents the voronoi diagram.
   **/
  Vertex right;

  /**
   * Seed value used during tree creation.
   **/
  static int seed;

  public Vertex() { }

  public Vertex(double x, double y)
  {
    super(x, y);
    left = null;
    right = null;
  }

  public void setLeft(Vertex l)
  {
    left = l;
  }
  
  public void setRight(Vertex r)
  {
    right = r;
  }

  public Vertex getLeft()
  {
    return left;
  }
  
  public Vertex getRight()
  {
    return right;
  }

  /**
   * Generate a voronoi diagram
   **/
  public static Vertex createPoints(int n, MyDouble curmax, int i)
  {
    if (n < 1 ) {
      return null;
    }

    Vertex cur = new Vertex();

    Vertex right = Vertex.createPoints(n/2, curmax, i);
    i -= n/2;
    cur.x = curmax.value * Math.exp(Math.log(Vertex.drand())/i);
    cur.y = Vertex.drand();
    cur.norm = cur.x * cur.x + cur.y*cur.y;
    cur.right = right;
    curmax.value = cur.X();
    Vertex left = Vertex.createPoints(n/2, curmax, i-1);
    
    cur.left = left;
    return cur;
  }


  /** 
   * Builds delaunay triangulation.
   **/
  public Edge buildDelaunayTriangulation(Vertex extra)
  {
    EdgePair retVal = buildDelaunay(extra);
    return retVal.getLeft(); 
  }  

  /**
   * Recursive delaunay triangulation procedure
   * Contains modifications for axis-switching division.
   **/
  public EdgePair buildDelaunay(Vertex extra)
  {
    EdgePair retval = null;
    if (getRight() != null && getLeft() != null)  {
      // more than three elements; do recursion
      Vertex minx = getLow(); 
      Vertex maxx = extra;

      EdgePair delright = getRight().buildDelaunay(extra);
      EdgePair delleft = getLeft().buildDelaunay(this);

      retval = Edge.doMerge(delleft.getLeft(), delleft.getRight(), 
			    delright.getLeft(), delright.getRight());

      Edge ldo = retval.getLeft();
      while (ldo.orig() != minx) { 
	ldo = ldo.rPrev();
      }
      Edge rdo = retval.getRight();
      while (rdo.orig() != maxx) {
	rdo = rdo.lPrev();
      }

      retval = new EdgePair(ldo, rdo);

    } else if (getLeft() == null) {	
      // two points
      Edge a = Edge.makeEdge(this, extra);
      retval = new EdgePair(a, a.symmetric());
    } else { 
      // left, !right  three points
      // 3 cases: triangles of 2 orientations, and 3 points on a line. */
      Vertex s1 = getLeft();
      Vertex s2 = this;
      Vertex s3 = extra;
      Edge a = Edge.makeEdge(s1, s2);
      Edge b = Edge.makeEdge(s2, s3);
      a.symmetric().splice(b);
      Edge c = b.connectLeft(a);
      if (s1.ccw(s3, s2)) {
	retval = new EdgePair(c.symmetric(), c);
      } else {
	retval = new EdgePair(a, b.symmetric());
	if (s1.ccw(s2, s3)) 
	  c.deleteEdge();
      }
    }
    return retval;
  }

  /**
   * Print the tree
   **/
  public void print()
  {
    Vertex tleft, tright;
    
    System.out.println("X=" + X() + " Y=" + Y());
    if (left == null)
      System.out.println("NULL");
    else
      left.print();
    if (right == null)
      System.out.println("NULL");
    else 
      right.print();
  }

  /**
   * Traverse down the left child to the end
   **/
  public Vertex getLow()
  {
    Vertex temp;
    Vertex tree = this;
    
    while ((temp=tree.getLeft()) != null)
      tree = temp;
    return tree;
  } 
  
  /****************************************************************/
  /*	Geometric primitives
  ****************************************************************/
  public boolean incircle(Vertex b, Vertex c, Vertex d)
    /* incircle, as in the Guibas-Stolfi paper. */
  {
    double adx, ady, bdx, bdy, cdx, cdy, dx, dy, anorm, bnorm, cnorm, dnorm;
    double dret ;
    Vertex loc_a,loc_b,loc_c,loc_d;

    int donedx,donedy,donednorm;
    loc_d = d; 
    dx = loc_d.X(); dy = loc_d.Y(); dnorm = loc_d.Norm();
    loc_a = this;
    adx = loc_a.X() - dx; ady = loc_a.Y() - dy; anorm = loc_a.Norm(); 
    loc_b = b;
    bdx = loc_b.X() - dx; bdy = loc_b.Y() - dy; bnorm = loc_b.Norm(); 
    loc_c = c;
    cdx = loc_c.X() - dx; cdy = loc_c.Y() - dy; cnorm = loc_c.Norm(); 
    dret =  (anorm - dnorm) * (bdx * cdy - bdy * cdx);
    dret += (bnorm - dnorm) * (cdx * ady - cdy * adx);
    dret += (cnorm - dnorm) * (adx * bdy - ady * bdx);
    return( (0.0 < dret) ? true : false );
  }
  
  public boolean ccw(Vertex b, Vertex c)
  /* TRUE iff this, B, C form a counterclockwise oriented triangle */
  {
    double dret ;
    double xa,ya,xb,yb,xc,yc;
    Vertex loc_a,loc_b,loc_c;
	
    int donexa,doneya,donexb,doneyb,donexc,doneyc;

    loc_a = this;
    xa = loc_a.X(); 
    ya = loc_a.Y();
    loc_b = b;
    xb = loc_b.X(); 
    yb = loc_b.Y();
    loc_c = c;
    xc = loc_c.X(); 
    yc = loc_c.Y();
    dret = (xa-xc)*(yb-yc) - (xb-xc)*(ya-yc);
    return( (dret  > 0.0)? true : false);
  }

  /**
   * A routine used by the random number generator
   **/
  public static int mult(int p,int q)
  {
    int p1, p0, q1, q0;
    int CONST_m1 = 10000;

    p1=p/CONST_m1; p0=p%CONST_m1;
    q1=q/CONST_m1; q0=q%CONST_m1;
    return (((p0*q1+p1*q0) % CONST_m1)*CONST_m1+p0*q0);
  }

  /**
   * Generate the nth random number
   **/
  public static int skiprand(int seed, int n)
  {
    for (; n != 0; n--) 
      seed=random(seed);
    return seed;
  }

  public static int random(int seed)
  {
    int CONST_b = 31415821;

    seed = mult(seed, CONST_b) + 1;
    return seed;
  }

  public static double drand()
  {
    double retval = ((double)(Vertex.seed = Vertex.random(Vertex.seed))) /
      (double) 2147483647;
    return retval;
  }
  
}

 
