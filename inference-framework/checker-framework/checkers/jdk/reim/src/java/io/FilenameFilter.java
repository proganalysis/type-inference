package java.io;

import checkers.inference2.reimN.quals.*;

public interface FilenameFilter {
    public boolean accept(@ReadRead File dir, String name);
}
