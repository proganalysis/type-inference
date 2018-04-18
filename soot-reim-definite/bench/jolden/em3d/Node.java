package em3d;
import java.util.Enumeration;
import java.util.Random;

/** 
 * This class implements nodes (both E- and H-nodes) of the EM graph. Sets
 * up random neighbors and propagates field values among neighbors.
 */
public final class Node 
{
  /**
   * The value of the node.
   **/
  double value;
  /**
   * The next node in the list.
   **/
  private Node next;
  /**
   * Array of nodes to which we send our value.
   **/
  Node[] toNodes;
  /**
   * Array of nodes from which we receive values.
   **/
  Node[] fromNodes;
  /**
   * Coefficients on the fromNodes edges
   **/
  double[] coeffs;
  /**
   * The number of fromNodes edges
   **/
  int fromCount;
  /**
   * Used to create the fromEdges - keeps track of the number of edges that have
   * been added
   **/
  int fromLength;

  /**
   * A random number generator.
   **/
  private static Random rand;

  /** 
   * Initialize the random number generator 
   **/
  public static void initSeed(long seed)
  {
    rand = new Random(seed);
  }

  /** 
   * Constructor for a node with given `degree'.   The value of the
   * node is initialized to a random value.
   **/
  public Node(int degree) 
  {
    value = rand.nextDouble();
    // create empty array for holding toNodes
    toNodes = new Node[degree];

    next = null;
    fromNodes = null;
    coeffs = null;
    fromCount = 0;
    fromLength = 0;
  }

  /**
   * Create the linked list of E or H nodes.  We create a table which is used
   * later to create links among the nodes.
   * @param size the no. of nodes to create
   * @param degree the out degree of each node
   * @return a table containing all the nodes.
   **/
  public static Node[] fillTable(int size, int degree)
  {
    Node[] table = new Node[size];

    Node prevNode = new Node(degree);
    table[0] = prevNode;
    for (int i = 1; i < size; i++) {
      Node curNode = new Node(degree);
      table[i] = curNode;
      prevNode.next = curNode;
      prevNode = curNode;
    }
    return table;
  }

  /** 
   * Create unique `degree' neighbors from the nodes given in nodeTable.
   * We do this by selecting a random node from the give nodeTable to
   * be neighbor. If this neighbor has been previously selected, then
   * a different random neighbor is chosen.
   * @param nodeTable the list of nodes to choose from.
   **/
  public void makeUniqueNeighbors(Node[] nodeTable)
  {
    for (int filled = 0; filled < toNodes.length; filled++) {
      int k;
      Node otherNode;

      do {
	// generate a random number in the correct range
	int index = rand.nextInt();
	if (index < 0) index = -index;
	index = index % nodeTable.length;

	// find a node with the random index in the given table
	otherNode = nodeTable[index];

	for (k = 0; k < filled; k++) {
	  if (otherNode == toNodes[filled]) break;
	}
      } while (k < filled);

      // other node is definitely unique among "filled" toNodes
      toNodes[filled] = otherNode;

      // update fromCount for the other node
      otherNode.fromCount++;
    }
  }

  /** 
   * Allocate the right number of FromNodes for this node. This
   * step can only happen once we know the right number of from nodes
   * to allocate. Can be done after unique neighbors are created and known.
   *
   * It also initializes random coefficients on the edges.
   **/
  public void makeFromNodes()
  {
    fromNodes = new Node[fromCount]; // nodes fill be filled in later
    coeffs = new double[fromCount];
  }

  /**
   * Fill in the fromNode field in "other" nodes which are pointed to
   * by this node.
   **/
  public void updateFromNodes()
  {
    for (int i = 0; i < toNodes.length; i++) {
      Node otherNode = toNodes[i];
      int count = otherNode.fromLength++;
      otherNode.fromNodes[count] = this;
      otherNode.coeffs[count] = rand.nextDouble();
    }
  }

  /** 
   * Get the new value of the current node based on its neighboring
   * from_nodes and coefficients.
   **/
  public void computeNewValue()
  {
    for (int i = 0; i < fromCount; i++) {
      value -= coeffs[i] * fromNodes[i].value;
    }
  }

  /**
   * Return an enumeration of the nodes.
   * @return an enumeration of the nodes.
   **/
  public Enumeration elements()
  {
    // a local class that implements the enumeration
    class Enumerate implements Enumeration {
      private Node current;
      public Enumerate() { this.current = Node.this; }
      public boolean hasMoreElements() { return (current != null); }
      public Object nextElement() {
	Object retval = current;
	current = current.next;
	return retval;
      }
    }
    return new Enumerate();
  }

  /**
   * Override the toString method to return the value of the node.
   * @return the value of the node.
   **/
  public String toString()
  {
    return "value " + value + ", from_count " + fromCount;
  }

}
