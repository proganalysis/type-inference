package java.util;
import checkers.inference.reim.quals.*;

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

  protected int next(@Readonly Random this, int bits)  {
      throw new RuntimeException("skeleton method");
    }

    public void nextBytes(@Readonly Random this, byte @Readonly [] bytes)  {
      throw new RuntimeException("skeleton method");
    }

    public int nextInt(@Readonly Random this)  {
      throw new RuntimeException("skeleton method");
    }

    public int nextInt(@Readonly Random this, int n)  {
      throw new RuntimeException("skeleton method");
    }

    public long nextLong(@Readonly Random this)  {
      throw new RuntimeException("skeleton method");
    }

    public boolean nextBoolean(@Readonly Random this)  {
      throw new RuntimeException("skeleton method");
    }

    public float nextFloat(@Readonly Random this)  {
      throw new RuntimeException("skeleton method");
    }

    public double nextDouble(@Readonly Random this)  {
      throw new RuntimeException("skeleton method");
    }

    synchronized public double nextGaussian(@Readonly Random this)  {
      throw new RuntimeException("skeleton method");
    }
}
