package java.io;

import checkers.inference2.reimN.quals.*;

public interface FileFilter {
    public boolean accept(@ReadRead File pathname);
}
