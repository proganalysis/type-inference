package java.io;
import checkers.inference.reim.quals.*;

import java.nio.channels.FileChannel;

public class FileOutputStream extends OutputStream {
    public FileOutputStream(String name) throws FileNotFoundException {
        throw new RuntimeException("skeleton method");
    }

    public FileOutputStream(String name, boolean append) throws FileNotFoundException {
        throw new RuntimeException("skeleton method");
    }

    public FileOutputStream(File file) throws FileNotFoundException {
        throw new RuntimeException("skeleton method");
    }

    public FileOutputStream(File file, boolean append) throws FileNotFoundException {
        throw new RuntimeException("skeleton method");
    }

    public FileOutputStream(FileDescriptor fdObj) {
        throw new RuntimeException("skeleton method");
    }

    public native void write(int b) throws IOException;

    public void write(@Readonly byte b[]) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void write(@Readonly byte b[], int off, int len) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void close() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public final FileDescriptor getFD()  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public FileChannel getChannel() {
        throw new RuntimeException("skeleton method");
    }

    protected void finalize() throws IOException {
        throw new RuntimeException("skeleton method");
    }
}
