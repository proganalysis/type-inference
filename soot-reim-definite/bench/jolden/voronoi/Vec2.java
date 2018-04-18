package voronoi;
import java.io.*;

/**
 * Vector Routines from CMU vision library.  
 * They are used only for the Voronoi Diagram, not the Delaunay Triagulation.
 * They are slow because of large call-by-value parameters.
 **/
public class Vec2 
{
  double x,y;
  double norm;

  public Vec2() {}
  
  public Vec2(double xx, double yy) 
  {
    x = xx;
    y = yy;
    norm =  x*x + y*y;
  }

  public double X()
  {
    return x;
  }

  public double Y()
  {
    return y;
  }

  public double Norm()
  {
    return norm;
  }
  
  public void setNorm(double d)
  {
    norm = d;
  }

  public String toString()
  {
    return x + " " + y;
  }

  public Vec2 circle_center(Vec2 b, Vec2 c)
  {
    Vec2 vv1 = b.sub(c);
    double d1 = vv1.magn();
    vv1 = sum(b);
    Vec2 vv2 = vv1.times(0.5);
    if (d1 < 0.0) /*there is no intersection point, the bisectors coincide. */
      return(vv2);
    else {
      Vec2 vv3 = b.sub(this);
      Vec2 vv4 = c.sub(this); 
      double d3 = vv3.cprod(vv4) ;
      double d2 = -2.0 * d3 ;
      Vec2 vv5 = c.sub(b);
      double d4 = vv5.dot(vv4);
      Vec2 vv6 = vv3.cross();
      Vec2 vv7 = vv6.times(d4/d2);
      return vv2.sum(vv7);
    }
  }



  /**
   * cprod: forms triple scalar product of [u,v,k], where k = u cross v 
   * (returns the magnitude of u cross v in space)
   **/
  public double cprod(Vec2 v)
  {
    return(x * v.y - y * v.x); 
  }

  /* V2_dot: vector dot product */

  public double dot(Vec2 v)
  {
    return(x * v.x + y * v.y);
  }

  /* V2_times: multiply a vector by a scalar */

  public Vec2 times(double c)
  {
    return (new Vec2(c*x, c*y));
  }

  /* V2_sum, V2_sub: Vector addition and subtraction */

  public Vec2 sum(Vec2 v)
  {
    return (new Vec2(x + v.x, y + v.y));
  }

  public Vec2 sub(Vec2 v)
  {
     return(new Vec2(x - v.x,y - v.y));
  }

/* V2_magn: magnitude of vector */

  public double magn()
  {
    return(Math.sqrt(x*x+y*y));
  }

  /* returns k X v (cross product).  this is a vector perpendicular to v */

  public Vec2 cross()
  {
    return(new Vec2(y,-x));
  }
}


 
