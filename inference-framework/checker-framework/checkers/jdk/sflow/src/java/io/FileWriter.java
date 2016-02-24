package java.io;
import checkers.inference.sflow.quals.Safe;
import checkers.inference.reim.quals.*;

public class FileWriter extends OutputStreamWriter {
    public FileWriter(/*@Safe*/ String fileName) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public FileWriter(/*@Safe*/ String fileName, boolean append) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public FileWriter(/*Safe*/ File file) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public FileWriter(/*Safe*/ File file, boolean append) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public FileWriter(/*Safe*/ FileDescriptor fd) {
        throw new RuntimeException("skeleton method");
    }
}
