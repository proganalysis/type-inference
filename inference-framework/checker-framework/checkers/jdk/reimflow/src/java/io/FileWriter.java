package java.io;
import checkers.inference.reimflow.quals.Tainted;
import checkers.inference.reim.quals.*;

public class FileWriter extends OutputStreamWriter {
    public FileWriter(/*@Tainted*/ String fileName) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public FileWriter(/*@Tainted*/ String fileName, boolean append) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public FileWriter(/*Tainted*/ File file) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public FileWriter(/*Tainted*/ File file, boolean append) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public FileWriter(/*Tainted*/ FileDescriptor fd) {
        throw new RuntimeException("skeleton method");
    }
}
