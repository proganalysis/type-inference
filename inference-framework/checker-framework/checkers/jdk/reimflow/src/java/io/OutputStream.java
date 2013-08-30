package java.io;

import checkers.inference.reim.quals.*;

public abstract class OutputStream implements Closeable, Flushable {
    public abstract void write(int b) throws IOException;

    public void write(byte @Readonly [] b) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void write(byte @Readonly [] b, int off, int len) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void flush() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void close() throws IOException {
        throw new RuntimeException("skeleton method");
    }

}
