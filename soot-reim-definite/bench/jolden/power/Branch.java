package power;
//import checkers.inference.ownership.quals.*;
/**
 * A class that represents a branch node in the power pricing
 * architecture.  A branch node is another type of intermediate node
 * that represents a split in the electrical power path.  The branch
 * nodes hang from the lateral nodes.
 * <b>
 * Each branch node contains a direct link to a set of customers.
 **/
public final class Branch 
{
  /**
   * Demand for the customers supported by the branch node.
   **/
  Demand D;
  double alpha = 0.0;
  double beta = 0.0;
  double R = 0.0001;
  double X = 0.00002;
  /**
   * A link to the next branch node that has the same parent (lateral) node.
   **/
  Branch nextBranch;
  /**
   * A list of customers - represented a leaf nodes.
   **/
  Leaf[] leaves;


  /**
   * Create all the branch nodes for a single lateral node.
   * Also, create the customers supported by this branch node
   *
   * @param num a counter to limit the branch nodes created for the lateral node
   * @param nleaves the nubmer of leafs to create per branch
   **/
  public Branch(int num, int nleaves) 
  {
    D = new  /*@ParPar*/  Demand();

    if (num <= 1) {
      if (num <= 0)
        throw new RuntimeException("Branch constructor with zero num");
      nextBranch = null;
    } else {
      nextBranch = new Branch(num-1, nleaves);
    }
 
    // fill in children
    leaves = new Leaf[nleaves];
    for (int k=0; k<nleaves; k++) {
      leaves[k] = new  /*@RepPar*/  Leaf();
    }
  }

  /**
   * Pass the prices down and compute the demand for the power system.
   * @param theta_R real power multiplier
   * @param theta_I reactive power multiplier
   * @param pi_R the real power price
   * @param pi_I the reactive power price
   * @return the demand for the customers supported by this branch
   **/
  public Demand compute(double theta_R, double theta_I, double pi_R, double pi_I) 
  {
    double new_pi_R = pi_R + alpha*(theta_R+(theta_I*X)/R);
    double new_pi_I = pi_I + beta*(theta_I+(theta_R*R)/X);
 
    Demand a1 = null;
    if (nextBranch != null)  {
      a1 = nextBranch.compute(theta_R, theta_I, new_pi_R, new_pi_I);
    }
 
    // Initialize and pass the prices down the tree
    D.reset();
    for (int i=0; i<leaves.length; i++) {
      D.increment(leaves[i].compute(new_pi_R, new_pi_I));
    }
    if (nextBranch != null) {
      D.increment(a1);
    }
 
    // pass demand up, P, Q
    double a = R*R + X*X;
    double b = 2*R*X*D.Q - 2*X*X*D.P - R;
    double c = R*D.Q - X*D.P;
    c = c*c + R*D.P;
    double root = (-b-Math.sqrt(b*b-4*a*c))/(2*a);
    D.Q = D.Q + ((root-D.P)*X)/R;
    D.P = root;
    // compute alpha, beta
    a = 2*R*D.P;
    b = 2*X*D.Q;
    alpha = a/(1-a-b);
    beta = b/(1-a-b);
 
    return D;
  }
}
