package voronoi;
import java.io.*;
import java.util.Stack;

/**
 * A Java implementation of the <tt>voronoi</tt> Olden benchmark. Voronoi
 * generates a random set of points and computes a Voronoi diagram for
 * the points.
 * <p>
 * <cite>
 * L. Guibas and J. Stolfi.  "General Subdivisions and Voronoi Diagrams"
 * ACM Trans. on Graphics 4(2):74-123, 1985.
 * </cite>
 * <p>
 * The Java version of voronoi (slightly) differs from the C version
 * in several ways.  The C version allocates an array of 4 edges and
 * uses pointer addition to implement quick rotate operations.  The
 * Java version does not use pointer addition to implement these
 * operations.
 **/
public class Voronoi 
{    
  /**
   * The number of points in the diagram
   **/
  private static int points = 0;
  /**
   * Set to true to print informative messages
   **/
  private static boolean printMsgs = false;
  /**
   * Set to true to print the voronoi diagram and its dual,
   * the delaunay diagram
   **/
  private static boolean printResults = false;

  /**
   * The main routine which creates the points and then performs
   * the delaunay triagulation.
   * @param args the command line parameters
   **/
  public static void main(String args[])
  {
    parseCmdLine(args);
    
    if (printMsgs)
      System.out.println("Getting " + points +  " points");

    long start0 = System.currentTimeMillis();
    Vertex.seed = 1023;
    Vertex extra = Vertex.createPoints(1, new MyDouble(1.0), points);
    Vertex point = Vertex.createPoints(points-1, new MyDouble(extra.X()),
				       points-1);
    long end0 = System.currentTimeMillis();

    if (printMsgs)
      System.out.println("Doing voronoi on " + points + " nodes"); 

    long start1 = System.currentTimeMillis();
    Edge edge = point.buildDelaunayTriangulation(extra);
    long end1 = System.currentTimeMillis();
  
    if (printResults)
      edge.outputVoronoiDiagram();

    if (printMsgs) {
      System.out.println("Build time " + (end0-start0)/1000.0);
      System.out.println("Compute  time " + (end1-start1)/1000.0);
      System.out.println("Total time " + (end1-start0)/1000.0);
    }

    System.out.println("Done!");
  }
  
  /**
   * Parse the command line options.
   * @param args the command line options.
   **/
  private static final void parseCmdLine(String args[])
  {
    int i = 0;
    String arg;
    
    while (i < args.length && args[i].startsWith("-")) {
      arg = args[i++];

      if (arg.equals("-n")) {
	if (i < args.length) {
	  points = new Integer(args[i++]).intValue();
	} else throw new RuntimeException("-n requires the number of points");
      } else if (arg.equals("-p")) {
	printResults = true;
      } else if (arg.equals("-m")) {
	printMsgs = true;
      } else if (arg.equals("-h")) {
	usage();
      }
    }
    if (points == 0) usage();
  }

  /**
   * The usage routine which describes the program options.
   **/
  private static final void usage()
  {
    System.err.println("usage: java Voronoi -n <points> [-p] [-m] [-h]");
    System.err.println("    -n the number of points in the diagram");
    System.err.println("    -p (print detailed results/messages - the voronoi diagram>)");
    System.err.println("    -v (print informative message)");
    System.err.println("    -h (this message)");
    System.exit(0);
  }

}
