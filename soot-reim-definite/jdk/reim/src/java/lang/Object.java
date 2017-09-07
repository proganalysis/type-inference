package java.lang;
import checkers.inference.reim.quals.*;

public class Object {

    private static native void registerNatives();
    @ReadonlyThis public final native Class<?> getClass() ;
    @ReadonlyThis public native int hashCode() ;

    @ReadonlyThis public boolean equals( @Readonly Object obj) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis protected native Object clone()  throws CloneNotSupportedException ;

    @ReadonlyThis public String toString()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public final native void notify() ;
    @ReadonlyThis public final native void notifyAll() ;
    @ReadonlyThis public final native void wait( long timeout)  throws InterruptedException;

    @ReadonlyThis public final void wait( long timeout, int nanos)  throws InterruptedException  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public final void wait()  throws InterruptedException  {
        throw new RuntimeException("skeleton method");
    }

    protected void finalize() throws Throwable { }
}
