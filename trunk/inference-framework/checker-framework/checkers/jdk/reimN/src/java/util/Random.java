package java.util;
import checkers.inference2.reimN.quals.*;

public
class Random implements java.io.Serializable {
    static final long serialVersionUID = 3905348978240129619L;
    public Random() {
      throw new RuntimeException("skeleton method");
    }
    public Random(long seed) {
      throw new RuntimeException("skeleton method");
    }

    synchronized public void setSeed(long seed) {
      throw new RuntimeException("skeleton method");
    }

  protected int next(@ReadRead Random this, int bits)  {
      throw new RuntimeException("skeleton method");
    }

    public void nextBytes(@ReadRead Random this, byte @ReadRead [] bytes)  {
      throw new RuntimeException("skeleton method");
    }

    public int nextInt(@ReadRead Random this)  {
      throw new RuntimeException("skeleton method");
    }

    public int nextInt(@ReadRead Random this, int n)  {
      throw new RuntimeException("skeleton method");
    }

    public long nextLong(@ReadRead Random this)  {
      throw new RuntimeException("skeleton method");
    }

    public boolean nextBoolean(@ReadRead Random this)  {
      throw new RuntimeException("skeleton method");
    }

    public float nextFloat(@ReadRead Random this)  {
      throw new RuntimeException("skeleton method");
    }

    public double nextDouble(@ReadRead Random this)  {
      throw new RuntimeException("skeleton method");
    }

    synchronized public double nextGaussian(@ReadRead Random this)  {
      throw new RuntimeException("skeleton method");
    }
}
