package java.lang;
import checkers.inference.reim.quals.*;

import java.io.IOException;

public interface Appendable {

    Appendable append(@Readonly CharSequence csq) throws IOException;
    Appendable append(@Readonly CharSequence csq, int start, int end) throws IOException;
    Appendable append(char c) throws IOException;
}
