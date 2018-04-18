package tsp;
import java.util.Random;

/**
 * A class that represents a node in a binary tree.  Each node represents
 * a city in the TSP benchmark.
 **/
public final class Tree
{
  /**
   * The number of nodes (cities) in this subtree
   **/
  private int sz;
  /**
   * The coordinates that this node represents
   **/
  private double x,y;
  /**
   * Left child of the tree
   **/
  private Tree  left;
  /**
   * Right child of tree
   **/
  private Tree  right;
  /**
   * The next pointer in a linked list of nodes in the subtree.  The list
   * contains the order of the cities to visit.
   **/
  private Tree  next;
  /**
   * The previous pointer in a linked list of nodes in the subtree. The list
   * contains the order of the cities to visit.
   **/
  private Tree  prev;

  // used by the random number generator
  private static final double  M_E2  = 7.3890560989306502274;
  private static final double  M_E3  = 20.08553692318766774179;
  private static final double  M_E6  = 403.42879349273512264299;
  private static final double  M_E12 = 162754.79141900392083592475;
 
  /**
   * Construct a Tree node (a city) with the specified size
   * @param size the number of nodes in the (sub)tree
   * @param x the x coordinate of the city
   * @param y the y coordinate of the city
   * @param left the left subtree
   * @param right the right subtree
   **/
  public Tree(int size, double x, double y, Tree l, Tree r)
  {
    sz = size;
    this.x = x;
    this.y = y;
    left = l;
    right = r;
    next = null;
    prev = null;
  }

  /**
   * Find Euclidean distance between this node and the specified node.
   * @param b the specified node
   * @return the Euclidean distance between two tree nodes.
   **/
  public double distance(Tree b) 
  {
    return (Math.sqrt((x-b.x)*(x-b.x)+(y-b.y)*(y-b.y)));
  }

  /**
   * Create a list of tree nodes.  Requires root to be the tail of the list.
   * Only fills in next field, not prev.
   * @return the linked list of nodes
   **/
  public Tree makeList() 
  {
    Tree myleft, myright;
    Tree tleft,tright;
    Tree retval = this;
    
    // head of left list
    if (left != null) 
      myleft = left.makeList();
    else
      myleft = null;

    // head of right list
    if (right != null)
      myright = right.makeList();
    else
      myright = null;

    if (myright != null) { 
      retval = myright; 
      right.next = this;
    }

    if (myleft != null) { 
      retval = myleft; 
      if (myright != null)
	left.next = myright;
      else
	left.next = this;
    }	
    next = null;
    
    return retval;
  }

  /**
   * Reverse the linked list.  Assumes that there is a dummy "prev"
   * node at the beginning.
   **/
  public void reverse() 
  {
    Tree prev = this.prev;
    prev.next = null;
    this.prev = null;
    Tree back = this;
    Tree tmp = this;
    // reverse the list for the other nodes
    Tree next;
    for (Tree t = this.next; t != null; back = t, t = next) {
      next = t.next;
      t.next = back;
      back.prev = t;
    }
    // reverse the list for this node
    tmp.next = prev;
    prev.prev = tmp;
  }

  /** 
   * Use closest-point heuristic from Cormen, Leiserson, and Rivest.
   * @return a 
   **/
  public Tree conquer() 
  {
    // create the list of nodes
    Tree t = makeList();
 
    // Create initial cycle 
    Tree cycle = t;
    t = t.next;
    cycle.next = cycle;
    cycle.prev = cycle;

    // Loop over remaining points 
    Tree donext;
    for (; t != null; t = donext) {
      donext = t.next; /* value won't be around later */
      Tree min = cycle;
      double mindist = t.distance(cycle);
      for (Tree tmp = cycle.next; tmp != cycle; tmp=tmp.next) {
	double test = tmp.distance(t);
	if (test < mindist) {
	  mindist = test;
	  min = tmp;
        } /* if */
      } /* for tmp... */

      Tree next = min.next;
      Tree prev = min.prev;

      double mintonext = min.distance(next);
      double mintoprev = min.distance(prev);
      double ttonext = t.distance(next);
      double ttoprev = t.distance(prev);

      if ((ttoprev - mintoprev) < (ttonext - mintonext)) {
	/* insert between min and prev */
	prev.next = t;
	t.next = min;
	t.prev = prev;
	min.prev = t;
      } else {
	next.prev = t;
	t.next = next;
	min.next = t;
	t.prev = min;
      }
    } /* for t... */

    return cycle;
  }

  /** 
   * Merge two cycles as per Karp.
   * @param a a subtree with one cycle
   * @param b a subtree with the other cycle
   **/
  public Tree merge(Tree a, Tree b) 
  {
    // Compute location for first cycle
    Tree   min = a;
    double mindist = distance(a);
    Tree   tmp = a;
    for (a = a.next; a != tmp; a = a.next) {
      double test = distance(a);
      if (test < mindist) {
	mindist = test;
	min = a;
      }
    }

    Tree next = min.next;
    Tree prev = min.prev;
    double mintonext = min.distance(next);
    double mintoprev = min.distance(prev);
    double ttonext   = distance(next);
    double ttoprev   = distance(prev);

    Tree p1, n1;
    double tton1, ttop1;
    if ((ttoprev - mintoprev) < (ttonext - mintonext)) {
      // would insert between min and prev
      p1 = prev;
      n1 = min;
      tton1 = mindist;
      ttop1 = ttoprev;
    } else { 
      // would insert between min and next
      p1 = min;
      n1 = next;
      ttop1 = mindist;
      tton1 = ttonext;
    }
 
    // Compute location for second cycle
    min = b;
    mindist = distance(b);
    tmp = b;
    for (b = b.next; b != tmp; b = b.next) {
      double test = distance(b);
      if (test < mindist) {
	mindist = test;
	min = b;
      }
    }

    next = min.next;
    prev = min.prev;
    mintonext = min.distance(next);
    mintoprev = min.distance(prev);
    ttonext = this.distance(next);
    ttoprev = this.distance(prev);
    
    Tree p2, n2;
    double tton2, ttop2;
    if ((ttoprev - mintoprev) < (ttonext - mintonext)) {
      // would insert between min and prev
      p2 = prev;
      n2 = min;
      tton2 = mindist;
      ttop2 = ttoprev;
    } else { 
      // would insert between min andn ext 
      p2 = min;
      n2 = next;
      ttop2 = mindist;
      tton2 = ttonext;
    }

    // Now we have 4 choices to complete:
    // 1:t,p1 t,p2 n1,n2
    // 2:t,p1 t,n2 n1,p2
    // 3:t,n1 t,p2 p1,n2
    // 4:t,n1 t,n2 p1,p2 
    double n1ton2 = n1.distance(n2);
    double n1top2 = n1.distance(p2);
    double p1ton2 = p1.distance(n2);
    double p1top2 = p1.distance(p2);

    mindist = ttop1 + ttop2 + n1ton2; 
    int choice = 1;

    double test = ttop1 + tton2 + n1top2;
    if (test < mindist) {
      choice = 2;
      mindist = test;
    }

    test = tton1 + ttop2 + p1ton2;
    if (test < mindist) {
      choice = 3;
      mindist = test;
    }
  
    test = tton1 + tton2 + p1top2;
    if (test < mindist) 
      choice = 4;

    switch (choice) {
    case 1:
      // 1:p1,this this,p2 n2,n1 -- reverse 2!
      n2.reverse();
      p1.next = this;
      this.prev = p1;
      this.next = p2;
      p2.prev = this;
      n2.next = n1;
      n1.prev = n2;
      break;
    case 2:
      // 2:p1,this this,n2 p2,n1 -- OK
      p1.next = this;
      this.prev = p1;
      this.next = n2;
      n2.prev = this;
      p2.next = n1;
      n1.prev = p2;
      break;
    case 3:
      // 3:p2,this this,n1 p1,n2 -- OK
      p2.next = this;
      this.prev = p2;
      this.next = n1;
      n1.prev = this;
      p1.next = n2;
      n2.prev = p1;
      break;
    case 4:
      // 4:n1,this this,n2 p2,p1 -- reverse 1!
      n1.reverse();
      n1.next = this;
      this.prev = n1;
      this.next = n2;
      n2.prev = this;
      p2.next = p1;
      p1.prev = p2;
      break;
    }
    return this;
  }

  /**
   * Compute TSP for the tree t. Use conquer for problems <= sz 
   * @param sz the cutoff point for using conquer vs. merge
   **/
  public Tree tsp(int sz) 
  {
    if (this.sz <= sz) return conquer();

    Tree leftval  = left.tsp(sz);
    Tree rightval = right.tsp(sz); 

    return merge(leftval, rightval);
  }

  /**
   * Print the list of cities to visit from the current node.  The
   * list is the result of computing the TSP problem.
   * The list for the root node (city) should contain every other node
   * (city).
   **/
  public void printVisitOrder()
  {
    System.out.println("x = " + x + " y = " + y);
    for (Tree tmp = next; tmp != this; tmp = tmp.next) {
      System.out.println("x = " + tmp.x + " y = " + tmp.y);
    }
  }

  //
  // static methods
  //

  /**
   * Return an estimate of median of n values distributed in [min,max)
   * @param min the minimum value 
   * @param max the maximum value
   * @param n 
   * @return an estimate of median of n values distributed in [min,max)
   **/
  private static double median(double min, double max, int n) 
  {
    // get random value in [0.0, 1.0)
    double t = (new Random()).nextDouble();
    
    double retval;
    if (t > 0.5) {
      retval = java.lang.Math.log(1.0-(2.0*(M_E12-1)*(t-0.5)/M_E12))/12.0;   
    } else {
      retval = -java.lang.Math.log(1.0-(2.0*(M_E12-1)*t/M_E12))/12.0;
    }
    // We now have something distributed on (-1.0,1.0)
    retval = (retval+1.0) * (max-min)/2.0;
    retval = retval + min;
    return retval;
  }

  /**
   * Get double uniformly distributed over [min,max) 
   * @return double uniformily distributed over [min,max)
   **/
  private static double uniform(double min, double max) 
  {
    // get random value between [0.0,1.0)
    double retval = (new Random()).nextDouble(); 
    retval = retval * (max-min);
    return retval + min;
  }

  /**
   * Builds a 2D tree of n nodes in specified range with dir as primary 
   * axis (false for x, true for y)
   *
   * @param n the size of the subtree to create
   * @param dir the primary axis
   * @param min_x the minimum x coordinate
   * @param max_x the maximum x coordinate
   * @param min_y the minimum y coordinate
   * @param max_y the maximum y coordinate
   * @return a reference to the root of the subtree
   **/
  public static Tree buildTree(int n, boolean dir, double min_x,
			       double max_x, double min_y, double max_y) 
  {
    if (n==0) return null;

    Tree left, right;
    double x, y;
    if (dir) {
      dir = !dir;
      double med = median(min_x,max_x,n);
      left = buildTree(n/2, dir, min_x, med, min_y, max_y);
      right = buildTree(n/2, dir, med, max_x, min_y, max_y);
      x = med;
      y = uniform(min_y, max_y);
    } else {
      dir = !dir;
      double med = median(min_y,max_y,n);
      left = buildTree(n/2, dir, min_x, max_x, min_y, med);
      right = buildTree(n/2, dir, min_x, max_x, med, max_y);
      y = med;
      x = uniform(min_x, max_x);
    }
    return new Tree(n, x, y, left, right);
  }

}
