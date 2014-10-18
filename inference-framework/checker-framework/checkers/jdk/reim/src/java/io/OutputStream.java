package java.io;

import checkers.inference2.reimN.quals.*;

public abstract class OutputStream implements Closeable, Flushable {
    public abstract void write(int b) throws IOException;

    public void write(byte @ReadRead [] b) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void write(byte @ReadRead [] b, int off, int len) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void flush() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void close() throws IOException {
        throw new RuntimeException("skeleton method");
    }

}
