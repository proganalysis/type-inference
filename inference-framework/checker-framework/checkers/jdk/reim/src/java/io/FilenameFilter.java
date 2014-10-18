package java.io;

import checkers.inference.reim.quals.*;

public interface FilenameFilter {
    public boolean accept(@Readonly File dir, String name);
}
