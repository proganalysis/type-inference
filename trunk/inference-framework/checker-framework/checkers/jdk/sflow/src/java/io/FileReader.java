package java.io;
import checkers.inference.sflow.quals.Tainted;
import checkers.inference.reim.quals.*;

public class FileReader extends InputStreamReader {

    public FileReader(/*@Tainted*/ String fileName) throws FileNotFoundException {
        throw new RuntimeException("skeleton method");
    }

    public FileReader(/*Tainted*/ File file) throws FileNotFoundException {
        throw new RuntimeException("skeleton method");
    }

    public FileReader(/*Tainted*/ FileDescriptor fd) {
        throw new RuntimeException("skeleton method");
    }

}
