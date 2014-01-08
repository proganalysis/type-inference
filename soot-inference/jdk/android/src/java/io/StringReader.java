package java.io;
import checkers.inference.reim.quals.*;

public class StringReader extends Reader {
    public StringReader(String s) {
        throw new RuntimeException("skeleton method");
    }

    public int read() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public int read(char cbuf[], int off, int len) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public long skip(long ns) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean ready()  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean markSupported()  {
        throw new RuntimeException("skeleton method"); 
    }

    public void mark(int readAheadLimit) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void reset() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void close() {
        throw new RuntimeException("skeleton method");
    }
}
