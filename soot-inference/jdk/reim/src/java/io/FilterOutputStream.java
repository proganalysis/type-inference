package java.io;

import checkers.inference.reim.quals.*;

public class FilterOutputStream extends OutputStream {
    protected OutputStream out;

    protected FilterOutputStream() {}
    public FilterOutputStream(OutputStream out) { }

    public void write(int b) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void write(@Readonly byte[] b) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void write(@Readonly byte[] b, int off, int len) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void flush() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void close() throws IOException {
        throw new RuntimeException("skeleton method");
    }
}
