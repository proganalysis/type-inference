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

    public Throwable(@Polyread Throwable this, String message, @Polyread Throwable cause)  {
        throw new RuntimeException("skeleton method");
    }

    public Throwable(@Polyread Throwable this, @Polyread Throwable cause)  {
        throw new RuntimeException("skeleton method");
    }

    public String getMessage(@Readonly Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getLocalizedMessage(@Readonly Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    public @Polyread Throwable getCause(@Polyread Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    public synchronized Throwable initCause(Throwable cause) {
        throw new RuntimeException("skeleton method");
    }

    public String toString(@Readonly Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    public void printStackTrace(@Readonly Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    public void printStackTrace(@Readonly Throwable this, PrintStream s)  {
        throw new RuntimeException("skeleton method");
    }

    private void printStackTraceAsCause(PrintStream s, StackTraceElement[] causedTrace) {
        throw new RuntimeException("skeleton method");
    }

    public void printStackTrace(@Readonly Throwable this, PrintWriter s)  {
        throw new RuntimeException("skeleton method");
    }

    private void printStackTraceAsCause(PrintWriter s, StackTraceElement[] causedTrace) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized native Throwable fillInStackTrace();

    public StackTraceElement[] getStackTrace(@Readonly Throwable this)  {
        throw new RuntimeException("skeleton method");
    }

    private synchronized StackTraceElement[] getOurStackTrace() {
        throw new RuntimeException("skeleton method");
    }

    public void setStackTrace(StackTraceElement @Readonly [] stackTrace) {
        throw new RuntimeException("skeleton method");
    }

    private native int getStackTraceDepth(@Readonly Throwable this) ;
    private native @Polyread StackTraceElement getStackTraceElement(@Polyread Throwable this, int index) ;

    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException {
        throw new RuntimeException("skeleton method");
    }
}
