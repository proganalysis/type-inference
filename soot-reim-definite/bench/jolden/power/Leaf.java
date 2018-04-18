package power;
/**
 * A class that represents a customer in the power system optimization
 * problem.  A customer is a leaf node in tree representation of the 
 * problem.
 **/
public final class Leaf 
{
  /**
   * Power demand for the customer
   **/
  Demand D;
  /**
   * Price for real power demand
   **/
  double pi_R;
  /**
   * Price for reaactive power demand
   **/
  double pi_I;

  public Leaf() 
  {
    D = new Demand(1.0, 1.0);
  }

  /**
   * Pass prices down and compute demand for the customer.
   * @return the power demand for the customer
   **/
  public Demand compute(double pi_R, double pi_I) 
  {
    D.optimizeNode(pi_R, pi_I);
 
    if (D.P < 0.0) {
      D.P = 0.0;
      D.Q = 0.0;
    }
    return D;
  }

}
