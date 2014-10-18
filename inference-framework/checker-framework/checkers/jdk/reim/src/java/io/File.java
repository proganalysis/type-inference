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

import checkers.inference2.reimN.quals.*;

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
    public File(@PolyPoly File this, @PolyPoly File parent, String child)  {
      throw new RuntimeException("skeleton method");
    }
    public File(@ReadRead URI uri) {
        throw new RuntimeException("skeleton method");
    }

    public String getName(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String getParent(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public File getParentFile(@PolyPoly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String getPath(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean isAbsolute(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String getAbsolutePath(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public @PolyPoly File getAbsoluteFile(@PolyPoly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String getCanonicalPath(@ReadRead File this)  throws IOException {
        throw new RuntimeException("skeleton method");
    }
    public @PolyPoly File getCanonicalFile(@PolyPoly File this)  throws IOException {
        throw new RuntimeException("skeleton method");
    }
    @Deprecated
    public @PolyPoly URL toURL(@PolyPoly File this)  throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }
    public @PolyPoly URI toURI(@PolyPoly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean canRead(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean canWrite(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean exists(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean isDirectory(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean isFile(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean isHidden(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public long lastModified(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public long length(@ReadRead File this)  {
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
    public String[] list(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String[] list(@ReadRead File this, FilenameFilter filter)  {
        throw new RuntimeException("skeleton method");
    }
    public @PolyPoly File [] listFiles(@PolyPoly File this)  {
        throw new RuntimeException("skeleton method");
    }
    public @PolyPoly File [] listFiles(@PolyPoly File this, FilenameFilter filter)  {
        throw new RuntimeException("skeleton method");
    }
    public @PolyPoly File [] listFiles(@PolyPoly File this, FileFilter filter)  {
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
    public boolean canExecute(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public static File[] listRoots() {
        throw new RuntimeException("skeleton method");
    }
    public long getTotalSpace(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public long getFreeSpace(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public long getUsableSpace(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
        throw new RuntimeException("skeleton method");
    }
    public static File createTempFile(String prefix, String suffix) throws IOException {
        throw new RuntimeException("skeleton method");
    }
//     public static File createTemporaryFile(String prefix, String suffx, @ReadRead FileAttribute<?>... attrs) {
//         throw new RuntimeException("skeleton method");
//     }
    public int compareTo(@ReadRead File this, @ReadRead File pathname)  {
        throw new RuntimeException("skeleton method");
    }
    public boolean equals(@ReadRead File this, @ReadRead Object obj)  {
        throw new RuntimeException("skeleton method");
    }
    public int hashCode(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
    public String toString(@ReadRead File this)  {
        throw new RuntimeException("skeleton method");
    }
//     public Path toPath(@ReadRead File this)  {
//         throw new RuntimeException("skeleton method");
//     }

}
