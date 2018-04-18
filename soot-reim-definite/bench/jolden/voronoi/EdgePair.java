package voronoi;
import java.io.*;
/**
 * A class that represents an edge pair
 **/
public class EdgePair 
{
  Edge left;
  Edge right;
  
  public EdgePair(Edge l, Edge r)
  {
    left = l;
    right = r;
  }

  public Edge getLeft()
  {
    return left;
  }

  public Edge getRight() 
  {
    return right;
  }

}  
