package java.util.zip;
import checkers.inference.reim.quals.*;

public class ZipEntry implements ZipConstants, Cloneable {
    public static final int STORED = 0;
    public static final int DEFLATED = 8;

    public ZipEntry(String name) {
        throw new RuntimeException("skeleton method");
    }

    public ZipEntry(@Readonly ZipEntry e) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getName()  {
        throw new RuntimeException("skeleton method");
    }

    public void setTime(long time) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getTime()  {
        throw new RuntimeException("skeleton method");
    }

    public void setSize(long size) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getSize()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getCompressedSize()  {
        throw new RuntimeException("skeleton method");
    }

    public void setCompressedSize(long csize) {
        throw new RuntimeException("skeleton method");
    }

    public void setCrc(long crc) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getCrc()  {
        throw new RuntimeException("skeleton method");
    }

    public void setMethod(int method) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int getMethod()  {
        throw new RuntimeException("skeleton method");
    }

    public void setExtra(byte[] extra) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public byte[] getExtra()  {
        throw new RuntimeException("skeleton method");
    }

    public void setComment(String comment) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getComment()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean isDirectory()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String toString()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int hashCode()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public Object clone()  {
        throw new RuntimeException("skeleton method");
    }
}
