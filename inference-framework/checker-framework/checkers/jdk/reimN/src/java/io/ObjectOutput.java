package java.io;

import checkers.inference2.reimN.quals.*;

public interface ObjectOutput extends DataOutput {
    public void close() throws IOException;
    public void flush() throws IOException;
    public void write(byte @ReadRead [] b) throws IOException;
    public void write(byte @ReadRead [] b, int off, int len) throws IOException;
    public void write(int b) throws IOException;
    public void writeObject(@ReadRead Object obj) throws IOException;
}
