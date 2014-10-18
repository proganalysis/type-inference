package java.lang;
import checkers.inference2.reimN.quals.*;

public class Object {

    private static native void registerNatives();
    public final native Class<?> getClass(@ReadRead Object this) ;
    public native int hashCode(@ReadRead Object this) ;

    public boolean equals(@ReadRead Object this, @ReadRead Object obj) {
        throw new RuntimeException("skeleton method");
    }

    protected native Object clone(@ReadRead Object this)  throws CloneNotSupportedException ;

    public String toString(@ReadRead Object this)  {
        throw new RuntimeException("skeleton method");
    }

    public final native void notify(@ReadRead Object this) ;
    public final native void notifyAll(@ReadRead Object this) ;
    public final native void wait(@ReadRead Object this, long timeout)  throws InterruptedException;

    public final void wait(@ReadRead Object this, long timeout, int nanos)  throws InterruptedException  {
        throw new RuntimeException("skeleton method");
    }

    public final void wait(@ReadRead Object this)  throws InterruptedException  {
        throw new RuntimeException("skeleton method");
    }

    protected void finalize() throws Throwable { }
}
