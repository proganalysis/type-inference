package java.lang;
import checkers.inference.reim.quals.*;

public class Object {

    private static native void registerNatives();
    public final native Class<?> getClass(@Readonly Object this) ;
    public native int hashCode(@Readonly Object this) ;

    public boolean equals(@Readonly Object this, @Readonly Object obj) {
        throw new RuntimeException("skeleton method");
    }

    protected native Object clone(@Readonly Object this)  throws CloneNotSupportedException ;

    public String toString(@Readonly Object this)  {
        throw new RuntimeException("skeleton method");
    }

    public final native void notify(@Readonly Object this) ;
    public final native void notifyAll(@Readonly Object this) ;
    public final native void wait(@Readonly Object this, long timeout)  throws InterruptedException;

    public final void wait(@Readonly Object this, long timeout, int nanos)  throws InterruptedException  {
        throw new RuntimeException("skeleton method");
    }

    public final void wait(@Readonly Object this)  throws InterruptedException  {
        throw new RuntimeException("skeleton method");
    }

    protected void finalize() throws Throwable { }
}
