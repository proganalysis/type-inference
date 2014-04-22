package java.io;
import checkers.inference.sflow.quals.Safe;
import checkers.inference.reim.quals.*;

public class FileReader extends InputStreamReader {

    public FileReader(/*-@Safe*/ String fileName) throws FileNotFoundException {
        throw new RuntimeException("skeleton method");
    }

    public FileReader(/*Safe*/ File file) throws FileNotFoundException {
        throw new RuntimeException("skeleton method");
    }

    public FileReader(/*Safe*/ FileDescriptor fd) {
        throw new RuntimeException("skeleton method");
    }

}
