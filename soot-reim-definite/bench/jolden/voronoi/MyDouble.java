package voronoi;
/**
 * A class that represents a wrapper around a double value so 
 * that we can use it as an 'out' parameter.  The java.lang.Double
 * class is immutable.
 **/
public class MyDouble
{
  public double value;
  MyDouble(double d)
  {
    value = d;
  }
  public String toString()
  {
    return Double.toString(value);
  }
}
