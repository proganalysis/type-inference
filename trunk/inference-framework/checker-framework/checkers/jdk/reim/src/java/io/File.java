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
    public File(@Polyread File this, @Polyread File parent, String child)  {
      throw new RuntimeException("skeleton method");
    }
    public File(@Readonly URI uri) {
        throw new RuntimeException("skeleton method");
    }

    public String getName(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String getParent(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public @Polyread File getParentFile(@Polyread File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String getPath(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean isAbsolute(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String getAbsolutePath(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public @Polyread File getAbsoluteFile(@Polyread File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String getCanonicalPath(@Readonly File this)  throws IOException {
        throw new RuntimeException("skeleton method");
    }
    public @Polyread File getCanonicalFile(@Polyread File this)  throws IOException {
        throw new RuntimeException("skeleton method");
    }
    @Deprecated
    public @Polyread URL toURL(@Polyread File this)  throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }
    public @Polyread URI toURI(@Polyread File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean canRead(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean canWrite(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean exists(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean isDirectory(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean isFile(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean isHidden(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public long lastModified(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public long length(@Readonly File this)  {
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
    public String[] list(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String[] list(@Readonly File this, FilenameFilter filter)  {
        throw new RuntimeException("skeleton method");
    }
    public @Polyread File [] listFiles(@Polyread File this)  {
        throw new RuntimeException("skeleton method");
    }
    public @Polyread File [] listFiles(@Polyread File this, FilenameFilter filter)  {
        throw new RuntimeException("skeleton method");
    }
    public @Polyread File [] listFiles(@Polyread File this, FileFilter filter)  {
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
    public boolean canExecute(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public static File[] listRoots() {
        throw new RuntimeException("skeleton method");
    }
    public long getTotalSpace(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public long getFreeSpace(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public long getUsableSpace(@Readonly File this)  {
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
    public int compareTo(@Readonly File this, @Readonly File pathname)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean equals(@Readonly File this, @Readonly Object obj)  {
        throw new RuntimeException("skeleton method");
    }
    public int hashCode(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String toString(@Readonly File this)  {
        throw new RuntimeException("skeleton method");
    }
//     public Path toPath(@Readonly File this)  {
//         throw new RuntimeException("skeleton method");
//     }

}
