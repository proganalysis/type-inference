package java.io;

import checkers.inference.reim.quals.*;

public abstract class Reader implements Readable, Closeable {

    protected @Readonly Object lock;

    protected Reader() {
        throw new RuntimeException("skeleton method");
    }

    protected Reader(@Readonly Object lock) {
       throw new RuntimeException("skeleton method");
    }

    public int read(java.nio.CharBuffer target) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public int read() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public int read(char cbuf[]) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    abstract public int read(char cbuf[], int off, int len) throws IOException;

    private static final int maxSkipBufferSize = 8192;

    private char skipBuffer[] = null;

    public long skip(long n) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public boolean ready(@Readonly Reader this)  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public boolean markSupported(@Readonly Reader this)  {
        throw new RuntimeException("skeleton method");
    }

    public void mark(int readAheadLimit) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void reset() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    abstract public void close() throws IOException;
}
