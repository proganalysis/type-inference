package java.util.zip;
import checkers.inference2.reimN.quals.*;

public class ZipEntry implements ZipConstants, Cloneable {
    public static final int STORED = 0;
    public static final int DEFLATED = 8;

    public ZipEntry(String name) {
        throw new RuntimeException("skeleton method");
    }

    public ZipEntry(@ReadRead ZipEntry e) {
        throw new RuntimeException("skeleton method");
    }

    public String getName(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setTime(long time) {
        throw new RuntimeException("skeleton method");
    }

    public long getTime(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setSize(long size) {
        throw new RuntimeException("skeleton method");
    }

    public long getSize(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public long getCompressedSize(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setCompressedSize(long csize) {
        throw new RuntimeException("skeleton method");
    }

    public void setCrc(long crc) {
        throw new RuntimeException("skeleton method");
    }

    public long getCrc(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setMethod(int method) {
        throw new RuntimeException("skeleton method");
    }

    public int getMethod(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setExtra(byte[] extra) {
        throw new RuntimeException("skeleton method");
    }

    public byte[] getExtra(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setComment(String comment) {
        throw new RuntimeException("skeleton method");
    }

    public String getComment(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public boolean isDirectory(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public String toString(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public int hashCode(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }

    public Object clone(@ReadRead ZipEntry this)  {
        throw new RuntimeException("skeleton method");
    }
}
