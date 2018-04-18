package power;
/**
 * A class that represents power demand.
 **/
public final class Demand
{
  /**
   * Real power demand.
   **/
  double P;
  /**
   * Reactive power demand.
   **/
  double Q;

  private static final double F_EPSILON = 0.000001;
  private static final double G_EPSILON = 0.000001;
  private static final double H_EPSILON = 0.000001;

  /**
   * Create an object that represents power demand and initialize the
   * power demans values.
   * @param toP the real power demand
   * @param toQ the reactive power demand
   **/
  public Demand(double toP, double toQ) 
  {
    P = toP;
    Q = toQ;
  }

  /**
   * Create an empry power demand object. 
   **/
  public Demand() 
  { 
    this(0.0, 0.0);
  }

  /**
   * Increment the demand.
   * @param frm the amount to increase the demand
   **/
  public void increment(Demand frm)
  {
    P += frm.P;
    Q += frm.Q;
  }

  /**
   * Reset the power demand values.
   **/
  public void reset()
  {
    P = 0.0;
    Q = 0.0;
  }

  /**
   * Add the demand values from the two inputs.
   * @param a1 the demand values for operand1
   * @param a2 the demand values for operand2
   **/
  public void add(Demand a1, Demand a2)
  {
    P = a1.P + a2.P;
    Q = a1.Q + a2.Q;
  }

  /**
   * Assign the demand from the specified value to this one.
   * @param frm the demand assigned to this object.
   **/
  public void assign(Demand frm)
  {
    P = frm.P;
    Q = frm.Q;
  }

  /**
   * Calculate the power demand given the prices.  The pricing problem
   * sets the price for each customer's power consumption so that
   * the economic efficiency of the whole community is maximied.
   *
   * @param pi_R price for real power
   * @param pi_I price for reactive power
   **/
  public void optimizeNode(double pi_R, double pi_I) 
  {
    double[] grad_f = new double[2];
    double[] grad_g = new double[2];
    double[] grad_h = new double[2];
    double[] dd_grad_f = new double[2];

    double g, h;
    do {
      // Move onto h=0 line
      h = findH();
      if (Math.abs(h) > H_EPSILON) {
	double magnitude = findGradientH(grad_h);
	double total     = h/magnitude;
	P -= total*grad_h[0];
	Q -= total*grad_h[1];
      }
 
      // Check that g is still valid 
      g = findG();
      if (g > G_EPSILON) {
	double magnitude = findGradientG(grad_g);
	findGradientH(grad_h);
	magnitude *= makeOrthogonal(grad_g, grad_h);
	double total = g/magnitude;
	P -= total*grad_g[0];
	Q -= total*grad_g[1];
      }
 
      // Maximize benefit
      double magnitude = findGradientF(pi_R, pi_I, grad_f);
      findDDGradF(pi_R, pi_I, dd_grad_f);
      double total = 0.0;
      for (int i = 0; i < 2; i++)
	total += grad_f[i] * dd_grad_f[i];
      magnitude /= Math.abs(total);
      findGradientH(grad_h);
      magnitude *= makeOrthogonal(grad_f, grad_h);
      findGradientG(grad_g);
      total=0.0;
      for (int i = 0; i < 2; i++)
	total += grad_f[i]*grad_g[i];
      if (total > 0) {
	double max_dist = - findG() / total;
	if (magnitude > max_dist)
	  magnitude = max_dist;
      }
      P += magnitude * grad_f[0];
      Q += magnitude * grad_f[1];
 
      h = findH();
      g = findG();
      findGradientF(pi_R, pi_I, grad_f);
      findGradientH(grad_h);
    } while (Math.abs(h)>H_EPSILON || g>G_EPSILON ||
	     (Math.abs(g)>G_EPSILON &&
	      Math.abs(grad_f[0]*grad_h[1]-grad_f[1]*grad_h[0])>F_EPSILON));
  }

  private double findG() 
  {
    return (P*P+Q*Q-0.8);
  }

  private double findH() 
  {
    return (P-5*Q);
  }
 
  private double findGradientF(double pi_R, double pi_I, double[] gradient) 
  {
    gradient[0] = 1/(1+P)-pi_R;
    gradient[1] = 1/(1+Q)-pi_I;

    double magnitude = 0.0;
    for (int i = 0; i < 2; i++)
      magnitude += gradient[i] * gradient[i];

    magnitude=Math.sqrt(magnitude);

    for (int i = 0; i < 2; i++)
      gradient[i] /= magnitude;
 
    return magnitude;
  }

  private double findGradientG(double[] gradient) 
  {
    gradient[0] = 2*P;
    gradient[1] = 2*Q;
    double magnitude = 0.0;
    for (int i=0; i<2; i++)
      magnitude += gradient[i] * gradient[i];

    magnitude = Math.sqrt(magnitude);

    for (int i=0; i<2; i++)
      gradient[i] /= magnitude;
 
    return magnitude;
  }

  private double findGradientH(double[] gradient) 
  {
    gradient[0]=1.0;
    gradient[1]=-5.0;
    double  magnitude=0.0;
    for (int i=0; i<2; i++)
        magnitude+=gradient[i]*gradient[i];
    magnitude=Math.sqrt(magnitude);
    for (int i=0; i<2; i++)
        gradient[i]/=magnitude;
 
    return magnitude;
  }
 
  private void findDDGradF(double pi_R, double pi_I, double[] dd_grad) 
  {
    double P_plus_1_inv = 1/(P+1);
    double Q_plus_1_inv = 1/(Q+1);
    double P_grad_term  = P_plus_1_inv-pi_R;
    double Q_grad_term  = Q_plus_1_inv-pi_I;
 
    double grad_mag = Math.sqrt(P_grad_term*P_grad_term+Q_grad_term*Q_grad_term);
 
    dd_grad[0] = -P_plus_1_inv*P_plus_1_inv*P_grad_term/grad_mag;
    dd_grad[1] = -Q_plus_1_inv*Q_plus_1_inv*Q_grad_term/grad_mag;
  }

  private double makeOrthogonal(double[] v_mod, double[] v_static) 
  {
    double total  = 0.0;
    for (int i=0; i<2; i++)
      total += v_mod[i]*v_static[i];

    double length = 0.0;
    for (int i=0; i<2; i++) {
      v_mod[i] -= total*v_static[i];
      length += v_mod[i]*v_mod[i];
    }
    length = Math.sqrt(length);

    for (int i=0; i<2; i++)
      v_mod[i] /= length;
 
    if (1-total*total<0)    // Roundoff error
        return 0;
 
    return Math.sqrt(1-total*total);
  }

}
