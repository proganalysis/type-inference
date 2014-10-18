package java.lang;
import checkers.inference2.reimN.quals.*;
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

    public Throwable(@PolyPoly Throwable this, String message, @PolyPoly Throwable cause)  {
        throw new RuntimeException("skeleton method");
    }

    public Throwable(@PolyPoly Throwable this, @PolyPoly Throwable cause)  {
        throw new RuntimeException("skeleton method");
    }

    public String getMessage(@ReadRead Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getLocalizedMessage(@ReadRead Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    public @PolyPoly Throwable getCause(@PolyPoly Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    public synchronized Throwable initCause(Throwable cause) {
        throw new RuntimeException("skeleton method");
    }

    public String toString(@ReadRead Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    public void printStackTrace(@ReadRead Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    public void printStackTrace(@ReadRead Throwable this, PrintStream s)  {
        throw new RuntimeException("skeleton method");
    }

    private void printStackTraceAsCause(PrintStream s, StackTraceElement[] causedTrace) {
        throw new RuntimeException("skeleton method");
    }

    public void printStackTrace(@ReadRead Throwable this, PrintWriter s)  {
        throw new RuntimeException("skeleton method");
    }

    private void printStackTraceAsCause(PrintWriter s, StackTraceElement[] causedTrace) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized native Throwable fillInStackTrace();

    public StackTraceElement[] getStackTrace(@ReadRead Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    private synchronized StackTraceElement[] getOurStackTrace() {
        throw new RuntimeException("skeleton method");
    }

    public void setStackTrace(StackTraceElement @ReadRead [] stackTrace) {
        throw new RuntimeException("skeleton method");
    }

    private native int getStackTraceDepth(@ReadRead Throwable this) ;
    private native @PolyPoly StackTraceElement getStackTraceElement(@PolyPoly Throwable this, int index) ;

    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException {
        throw new RuntimeException("skeleton method");
    }
}
