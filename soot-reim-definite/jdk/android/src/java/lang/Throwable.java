package java.lang;
import checkers.inference.reim.quals.*;
import  java.io.*;

public class Throwable implements Serializable {

    private static final long serialVersionUID = -3042686055658047285L;
    private transient Object backtrace;
    private String detailMessage;
    private Throwable cause = this;
    private StackTraceElement[] stackTrace;

    public Throwable() {
        throw new RuntimeException("skeleton method");
    }

    public Throwable(String message) {
        throw new RuntimeException("skeleton method");
    }

    @PolyreadThis public Throwable( String message, @Polyread Throwable cause)  {
        throw new RuntimeException("skeleton method");
    }

    @PolyreadThis public Throwable( @Polyread Throwable cause)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getMessage()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getLocalizedMessage()  {
        throw new RuntimeException("skeleton method");
    }

    @PolyreadThis public @Polyread Throwable getCause()  {
        throw new RuntimeException("skeleton method");
    }

    public synchronized Throwable initCause(Throwable cause) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String toString()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public void printStackTrace()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public void printStackTrace( PrintStream s)  {
        throw new RuntimeException("skeleton method");
    }

    private void printStackTraceAsCause(PrintStream s, StackTraceElement[] causedTrace) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public void printStackTrace( PrintWriter s)  {
        throw new RuntimeException("skeleton method");
    }

    private void printStackTraceAsCause(PrintWriter s, StackTraceElement[] causedTrace) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized native Throwable fillInStackTrace();

    @ReadonlyThis public StackTraceElement[] getStackTrace()  {
        throw new RuntimeException("skeleton method");
    }

    private synchronized StackTraceElement[] getOurStackTrace() {
        throw new RuntimeException("skeleton method");
    }

    public void setStackTrace(@Readonly StackTraceElement[] stackTrace) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis private native int getStackTraceDepth() ;
    @PolyreadThis private native @Polyread StackTraceElement getStackTraceElement( int index) ;

    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException {
        throw new RuntimeException("skeleton method");
    }
}
