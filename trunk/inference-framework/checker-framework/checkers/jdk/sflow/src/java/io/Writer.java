package java.io;

import checkers.inference.reim.quals.*;

public abstract class Writer implements Appendable, Closeable, Flushable {

    private char[] writeBuffer;
    private final int writeBufferSize = 1024;

    protected @Readonly Object lock;

    protected Writer() {
        throw new RuntimeException("skeleton method");
    }

    protected Writer(@Readonly Object lock) {
        throw new RuntimeException("skeleton method");
    }

    public void write(int c) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void write(char @Readonly [] cbuf) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    abstract public void write(char @Readonly [] cbuf, int off, int len) throws IOException;

    public void write(String str) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void write(String str, int off, int len) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public Writer append(@Readonly CharSequence csq) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public Writer append(@Readonly CharSequence csq, int start, int end) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public Writer append(char c) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    abstract public void flush() throws IOException;
    abstract public void close() throws IOException;
}
