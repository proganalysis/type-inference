package power;

//import checkers.inference.ownership.quals.*;
/**
 * A class that represents a lateral node in the power pricing problem.
 * The lateral nodes is an intermediate node that represents a switch,
 * tappoints, or transformer.  It hangs from the root node (the power
 * substation).
 * <p>
 * Each lateral node is the head in a line of branch nodes.
 **/
public final class Lateral 
{
  /**
   * Demand for the customers supported by the lateral node.
   **/
  Demand  D;
  double  alpha = 0.0;
  double  beta = 0.0;
  double  R = 1/300000.0;
  double  X = 0.000001;
  /**
   * The next lateral that shares the same parent (root) node.
   **/
  Lateral next_lateral;
  /**
   * The branch nodes that are supported by the lateral node.
   **/
  Branch  branch;

  /**
   * Create all the lateral nodes for a single root node.
   * @param num the child number of the lateral wrt the root 
   * @param nbranches the number of branch nodes per lateral.
   * @param nleaves the number of leaf nodes per branch.
   **/
  public Lateral(int num, int nbranches, int nleaves)
  {
    D = new  /*@ParPar*/  Demand();

    // create a linked list of the lateral nodes
    if (num <= 1) {
      if (num <= 0) 
        throw new RuntimeException("Lateral constructor with zero num");
      next_lateral = null;
    } else {
      next_lateral = new Lateral(num-1, nbranches, nleaves);
    }

    // create the branch nodes
    branch = new  /*@RepPar*/  Branch(nbranches, nleaves);
  }

  /**
   * Pass prices down and compute demand for the power system.
   * @param theta_R real power demand multiplier
   * @param theta_I reactive power demand multiplier
   * @param pi_R price of real power demand
   * @param pi_I price of reactive power demand
   * @return the demand for the customers supported by this lateral
   **/
  public Demand compute(double theta_R, double theta_I, double pi_R, double pi_I) 
  {
    // generate the new prices and pass them down to the customers
    double new_pi_R = pi_R + alpha*(theta_R+(theta_I*X)/R);
    double new_pi_I = pi_I + beta*(theta_I+(theta_R*R)/X);

    Demand a1;
    if (next_lateral != null)
      a1 = next_lateral.compute(theta_R,theta_I,new_pi_R,new_pi_I);
    else
      a1 = null;
 
    Demand a2 = branch.compute(theta_R,theta_I,new_pi_R,new_pi_I);
 
    if (next_lateral != null) {
      D.add(a1, a2);
    } else {
      D.assign(a2);
    }

    // compute the new power demand values P,Q
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
