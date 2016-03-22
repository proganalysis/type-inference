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

  @ReadonlyThis protected int next(int bits)  {
      throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public void nextBytes(@Readonly byte[] bytes)  {
      throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int nextInt()  {
      throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int nextInt(int n)  {
      throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long nextLong()  {
      throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean nextBoolean()  {
      throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public float nextFloat()  {
      throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public double nextDouble()  {
      throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis synchronized public double nextGaussian()  {
      throw new RuntimeException("skeleton method");
    }
}
