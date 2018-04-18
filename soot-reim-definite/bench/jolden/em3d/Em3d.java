package em3d;
import java.io.*;

/** 
 * Java implementation of the <tt>em3d</tt> Olden benchmark.  This Olden
 * benchmark models the propagation of electromagnetic waves through
 * objects in 3 dimensions. It is a simple computation on an irregular
 * bipartite graph containing nodes representing electric and magnetic
 * field values.
 *
 * <p><cite>
 * D. Culler, A. Dusseau, S. Goldstein, A. Krishnamurthy, S. Lumetta, T. von 
 * Eicken and K. Yelick. "Parallel Programming in Split-C".  Supercomputing
 * 1993, pages 262-273.
 * </cite>
 **/
public class Em3d 
{
  /**
   * The number of nodes (E and H) 
   **/
  private static int numNodes = 0;
  /**
   * The out-degree of each node.
   **/
  private static int numDegree = 0;
  /**
   * The number of compute iterations 
   **/
  private static int numIter = 1;
  /**
   * Should we print the results and other runtime messages
   **/
  private static boolean printResult = false;
  /**
   * Print information messages?
   **/
  private static boolean printMsgs = false;

  /**
   * The main roitine that creates the irregular, linked data structure
   * that represents the electric and magnetic fields and propagates the
   * waves through the graph.
   * @param args the command line arguments
   **/
  public static final void main(String args[])
  {
    parseCmdLine(args);

    if (printMsgs) 
      System.out.println("Initializing em3d random graph...");
    long start0 = System.currentTimeMillis();
    BiGraph graph = BiGraph.create(numNodes, numDegree, printResult);
    long end0 = System.currentTimeMillis();

    // compute a single iteration of electro-magnetic propagation
    if (printMsgs) 
      System.out.println("Propagating field values for " + numIter + 
			 " iteration(s)...");
    long start1 = System.currentTimeMillis();
    for (int i = 0; i < numIter; i++) {
      graph.compute();
    }
    long end1 = System.currentTimeMillis();

    // print current field values
    if (printResult)
      System.out.println(graph);

    if (printMsgs) {
      System.out.println("EM3D build time " + (end0 - start0)/1000.0);
      System.out.println("EM3D compute time " + (end1 - start1)/1000.0);
      System.out.println("EM3D total time " + (end1 - start0)/1000.0);
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

      // check for options that require arguments
      if (arg.equals("-n")) {
        if (i < args.length) {
          numNodes = new Integer(args[i++]).intValue();
        } else throw new Error("-n requires the number of nodes");
      } else if (arg.equals("-d")) {
	if (i < args.length) {
	  numDegree = new Integer(args[i++]).intValue();
	} else throw new Error("-d requires the out degree");
      } else if (arg.equals("-i")) {
	if (i < args.length) {
	  numIter = new Integer(args[i++]).intValue();
	} else throw new Error("-i requires the number of iterations");
      } else if (arg.equals("-p")) {
        printResult = true;
      } else if (arg.equals("-m")) {
        printMsgs = true;
      } else if (arg.equals("-h")) {
	usage();
      }
    }
    if (numNodes == 0 || numDegree == 0) usage();
  }

  /**
   * The usage routine which describes the program options.
   **/
  private static  final void usage()
  {
    System.out.println("usage: java Em3d -n <nodes> -d <degree> [-p] [-m] [-h]");
    System.out.println("    -n the number of nodes");
    System.out.println("    -d the out-degree of each node");
    System.out.println("    -i the number of iterations");
    System.out.println("    -p (print detailed results)");
    System.out.println("    -m (print informative messages)");
    System.out.println("    -h (this message)");
    System.exit(0);
  }

}
