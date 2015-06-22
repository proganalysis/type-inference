package java.io;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.security.AccessController;
import java.security.SecureRandom;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.attribute.FileAttribute;

import checkers.inference.reim.quals.*;

public class File implements Serializable, Comparable<File> {
    private static final long serialVersionUID = 0L;

    static private FileSystem fs = FileSystem.getFileSystem();

    public static final char separatorChar = fs.getSeparator();
    public static final String separator = "" + separatorChar;
    public static final char pathSeparatorChar = fs.getPathSeparator();
    public static final String pathSeparator = "" + pathSeparatorChar;

    public File(String pathname) {
        throw new RuntimeException("skeleton method");
    }
    public File(String parent, String child) {
        throw new RuntimeException("skeleton method");
    }
    @PolyreadThis public File( @Polyread File parent, String child)  {
      throw new RuntimeException("skeleton method");
    }
    public File(@Readonly URI uri) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getName()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public String getParent()  {
        throw new RuntimeException("skeleton method");
    }
    @PolyreadThis public File getParentFile()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public String getPath()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public boolean isAbsolute()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public String getAbsolutePath()  {
        throw new RuntimeException("skeleton method");
    }
    @PolyreadThis public @Polyread File getAbsoluteFile()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public String getCanonicalPath()  throws IOException {
        throw new RuntimeException("skeleton method");
    }
    @PolyreadThis public @Polyread File getCanonicalFile()  throws IOException {
        throw new RuntimeException("skeleton method");
    }
    @Deprecated
    @PolyreadThis public @Polyread URL toURL()  throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }
    @PolyreadThis public @Polyread URI toURI()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public boolean canRead()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public boolean canWrite()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public boolean exists()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public boolean isDirectory()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public boolean isFile()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public boolean isHidden()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public long lastModified()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public long length()  {
        throw new RuntimeException("skeleton method");
    }
    public boolean createNewFile() throws IOException {
        throw new RuntimeException("skeleton method");
    }
    public boolean delete() {
        throw new RuntimeException("skeleton method");
    }
    public void deleteOnExit() {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public String[] list()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public String[] list( FilenameFilter filter)  {
        throw new RuntimeException("skeleton method");
    }
    @PolyreadThis public @Polyread File [] listFiles()  {
        throw new RuntimeException("skeleton method");
    }
    @PolyreadThis public @Polyread File [] listFiles( FilenameFilter filter)  {
        throw new RuntimeException("skeleton method");
    }
    @PolyreadThis public @Polyread File [] listFiles( FileFilter filter)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean mkdir() {
        throw new RuntimeException("skeleton method");
    }
    public boolean mkdirs() {
        throw new RuntimeException("skeleton method");
    }
    public boolean renameTo(File dest) {
        throw new RuntimeException("skeleton method");
    }
    public boolean setLastModified(long time) {
        throw new RuntimeException("skeleton method");
    }
    public boolean setReadOnly() {
        throw new RuntimeException("skeleton method");
    }
    public boolean setWritable(boolean writable, boolean ownerOnly) {
        throw new RuntimeException("skeleton method");
    }
    public boolean setWritable(boolean writable) {
        throw new RuntimeException("skeleton method");
    }
    public boolean setReadable(boolean readable, boolean ownerOnly) {
        throw new RuntimeException("skeleton method");
    }
    public boolean setReadable(boolean readable) {
        throw new RuntimeException("skeleton method");
    }
    public boolean setExecutable(boolean executable, boolean ownerOnly) {
        throw new RuntimeException("skeleton method");
    }
    public boolean setExecutable(boolean executable) {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public boolean canExecute()  {
        throw new RuntimeException("skeleton method");
    }
    public static File[] listRoots() {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public long getTotalSpace()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public long getFreeSpace()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public long getUsableSpace()  {
        throw new RuntimeException("skeleton method");
    }
    public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
        throw new RuntimeException("skeleton method");
    }
    public static File createTempFile(String prefix, String suffix) throws IOException {
        throw new RuntimeException("skeleton method");
    }
//     public static File createTemporaryFile(String prefix, String suffx, @Readonly FileAttribute<?>... attrs) {
//         throw new RuntimeException("skeleton method");
//     }
    @ReadonlyThis public int compareTo( @Readonly File pathname)  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public boolean equals( @Readonly Object obj)  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public int hashCode()  {
        throw new RuntimeException("skeleton method");
    }
    @ReadonlyThis public String toString()  {
        throw new RuntimeException("skeleton method");
    }
//     public Path toPath()  {
//         throw new RuntimeException("skeleton method");
//     }

}
