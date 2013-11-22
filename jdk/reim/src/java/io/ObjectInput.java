package java.io;

import checkers.inference.reim.quals.*;

public interface ObjectInput extends DataInput {
    public int available(@Readonly ObjectInput this)  throws IOException;
    public void close() throws IOException;
    public int read() throws IOException;
    public int read(byte[] b) throws IOException;
    public int read(byte[] b, int off, int len)  throws IOException;
    public Object readObject() throws ClassNotFoundException, IOException;
    public long skip(long n) throws IOException;
}
