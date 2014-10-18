package java.io;
import checkers.inference.reim.quals.*;

public class StringWriter extends Writer {
    public StringWriter() {
        throw new RuntimeException("skeleton method");
    }

    public StringWriter(int initialSize) {
        throw new RuntimeException("skeleton method");
    }

    public void write(int c) {
        throw new RuntimeException("skeleton method");
    }

    public void write(char cbuf @Readonly [], int off, int len) {
        throw new RuntimeException("skeleton method");
    }

    public void write(String str) {
        throw new RuntimeException("skeleton method");
    }

    public void write(String str, int off, int len) {
        throw new RuntimeException("skeleton method");
    }

    public StringWriter append(@Readonly CharSequence csq) {
        throw new RuntimeException("skeleton method");
    }

    public StringWriter append(@Readonly CharSequence csq, int start, int end) {
        throw new RuntimeException("skeleton method");
    }

    public StringWriter append(char c) {
        throw new RuntimeException("skeleton method");
    }

    public String toString(@Readonly StringWriter this)  {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer getBuffer(@Readonly StringWriter this)  {
        throw new RuntimeException("skeleton method");
    }

    public void flush() {
        throw new RuntimeException("skeleton method");
    }

    public void close() throws IOException {
        throw new RuntimeException("skeleton method");
    }
}
