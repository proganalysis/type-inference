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

    public String getName(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setTime(long time) {
        throw new RuntimeException("skeleton method");
    }

    public long getTime(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setSize(long size) {
        throw new RuntimeException("skeleton method");
    }

    public long getSize(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public long getCompressedSize(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setCompressedSize(long csize) {
        throw new RuntimeException("skeleton method");
    }

    public void setCrc(long crc) {
        throw new RuntimeException("skeleton method");
    }

    public long getCrc(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setMethod(int method) {
        throw new RuntimeException("skeleton method");
    }

    public int getMethod(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setExtra(byte[] extra) {
        throw new RuntimeException("skeleton method");
    }

    public byte[] getExtra(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setComment(String comment) {
        throw new RuntimeException("skeleton method");
    }

    public String getComment(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public boolean isDirectory(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public String toString(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public int hashCode(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public Object clone(@Readonly ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }
}
