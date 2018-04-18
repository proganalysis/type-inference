package em3d;
import java.util.Enumeration;

/** 
 * A class that represents the irregular bipartite graph used in
 * EM3D.  The graph contains two linked structures that represent the
 * E nodes and the N nodes in the application.
 **/
public final class BiGraph
{
  /**
   * Nodes that represent the electrical field.
   **/
  Node eNodes;
  /**
   * Nodes that representhe the magnetic field.
   **/
  Node hNodes;

  /**
   * Construct the bipartite graph.
   * @param e the nodes representing the electric fields
   * @param h the nodes representing the magnetic fields
   **/ 
  public BiGraph(Node e, Node h)
  {
    eNodes = e;
    hNodes = h;
  }

  /**
   * Create the bi graph that contains the linked list of
   * e and h nodes.
   * @param numNodes the number of nodes to create
   * @param numDegree the out-degree of each node
   * @param verbose should we print out runtime messages
   * @return the bi graph that we've created.
   **/
  public static BiGraph create(int numNodes, int numDegree, boolean verbose)
  {
    Node.initSeed(783);

    // making nodes (we create a table)
    if (verbose) System.out.println("making nodes (tables in orig. version)");
    Node[] hTable = Node.fillTable(numNodes, numDegree);
    Node[] eTable = Node.fillTable(numNodes, numDegree);

    // making neighbors
    if (verbose) System.out.println("updating from and coeffs");
    for (Enumeration e = hTable[0].elements(); e.hasMoreElements(); ) {
      Node n = (Node) e.nextElement();
      n.makeUniqueNeighbors(eTable);
    }
    for (Enumeration e = eTable[0].elements(); e.hasMoreElements(); ) {
      Node n = (Node) e.nextElement();
      n.makeUniqueNeighbors(hTable);
    }

    // Create the fromNodes and coeff field
    if (verbose) System.out.println("filling from fields");
    for (Enumeration e = hTable[0].elements(); e.hasMoreElements(); ) {
      Node n = (Node) e.nextElement();
      n.makeFromNodes();
    }
    for (Enumeration e = eTable[0].elements(); e.hasMoreElements(); ) {
      Node n = (Node) e.nextElement();
      n.makeFromNodes();
    }

    // Update the fromNodes
    for (Enumeration e = hTable[0].elements(); e.hasMoreElements(); ) {
      Node n = (Node) e.nextElement();
      n.updateFromNodes();
    }
    for (Enumeration e = eTable[0].elements(); e.hasMoreElements(); ) {
      Node n = (Node) e.nextElement();
      n.updateFromNodes();
    }

    BiGraph g = new BiGraph(eTable[0], hTable[0]);
    return g;
  }

  /** 
   * Update the field values of e-nodes based on the values of
   * neighboring h-nodes and vice-versa.
   **/
  public void compute()
  {
    for (Enumeration e = eNodes.elements(); e.hasMoreElements(); ) {
      Node n = (Node) e.nextElement();
      n.computeNewValue();
    }
    for (Enumeration e = hNodes.elements(); e.hasMoreElements(); ) {
      Node n = (Node) e.nextElement();
      n.computeNewValue();
    }
  }

  /**
   * Override the toString method to print out the values of the e and h nodes.
   * @return a string contain the values of the e and h nodes.
   **/
  public String toString()
  {
    StringBuffer retval = new StringBuffer();
    for (Enumeration e = eNodes.elements(); e.hasMoreElements(); ) {
      Node n = (Node) e.nextElement();
      retval.append("E: " + n + "\n");
    }
    
    for (Enumeration e = hNodes.elements(); e.hasMoreElements(); ) {
      Node n = (Node) e.nextElement();
      retval.append("H: " + n + "\n");
    }
    return retval.toString();
  }

}
