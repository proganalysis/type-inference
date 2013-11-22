package java.io;

import checkers.inference.reim.quals.*;

public interface FileFilter {
    public boolean accept(@Readonly File pathname);
}
