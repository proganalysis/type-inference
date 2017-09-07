package java.io;
import checkers.inference.reim.quals.*;

public abstract class InputStream implements Closeable {
    public abstract int read() throws IOException;

    public int read(byte b[]) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public int read(byte b[], int off, int len) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public long skip(long n) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int available()  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void close() throws IOException {}

    public synchronized void mark(int readlimit) {}

    public synchronized void reset() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean markSupported()  {
        throw new RuntimeException("skeleton method");
    }
}
