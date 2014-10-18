package java.lang;
import checkers.inference2.reimN.quals.*;

import java.io.IOException;

public interface Appendable {

    Appendable append(@ReadRead CharSequence csq) throws IOException;
    Appendable append(@ReadRead CharSequence csq, int start, int end) throws IOException;
    Appendable append(char c) throws IOException;
}
