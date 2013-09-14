package java.net;
import checkers.inference.reim.quals.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.List;
import java.security.Permission;

public abstract class URLConnection {
    protected URL url;
    protected boolean doInput;
    protected boolean doOutput;
    protected boolean allowUserInteraction;
    protected boolean useCaches;
    protected long ifModifiedSince;
    protected boolean connected;


    public static synchronized FileNameMap getFileNameMap() {
        throw new RuntimeException("skeleton method");
    }

    public static void setFileNameMap(@Readonly FileNameMap map) {
        throw new RuntimeException("skeleton method");
    }

    abstract public void connect() throws IOException;

    public void setConnectTimeout(int timeout) {
        throw new RuntimeException("skeleton method");
    }

    public int getConnectTimeout(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setReadTimeout(int timeout) {
        throw new RuntimeException("skeleton method");
    }

    public int getReadTimeout(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    protected URLConnection(@Readonly URLConnection this, @Readonly URL url)  {
        throw new RuntimeException("skeleton method");
    }

    public URL getURL(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public int getContentLength(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public long getContentLengthLong(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getContentType(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getContentEncoding(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public long getExpiration(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public long getDate(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public long getLastModified(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getHeaderField(@Readonly URLConnection this, String name)  {
        throw new RuntimeException("skeleton method");
    }

    public @Readonly Map<String, @Readonly List<String>> getHeaderFields(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public int getHeaderFieldInt(@Readonly URLConnection this, String name, int Default)  {
        throw new RuntimeException("skeleton method");
    }

    public long getHeaderFieldLong(@Readonly URLConnection this, String name, long Default)  {
        throw new RuntimeException("skeleton method");
    }

    public long getHeaderFieldDate(@Readonly URLConnection this, String name, long Default)  {
        throw new RuntimeException("skeleton method");
    }

    public String getHeaderFieldKey(@Readonly URLConnection this, int n)  {
        throw new RuntimeException("skeleton method");
    }

    public String getHeaderField(@Readonly URLConnection this, int n)  {
        throw new RuntimeException("skeleton method");
    }

    public Object getContent() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public Object getContent(Class[] classes) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public Permission getPermission(@Readonly URLConnection this)  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public InputStream getInputStream() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public OutputStream getOutputStream() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public String toString(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setDoInput(boolean doinput) {
        throw new RuntimeException("skeleton method");
    }

    public boolean getDoInput(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setDoOutput(boolean dooutput) {
        throw new RuntimeException("skeleton method");
    }

    public boolean getDoOutput(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setAllowUserInteraction(boolean allowuserinteraction) {
        throw new RuntimeException("skeleton method");
    }

    public boolean getAllowUserInteraction(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public static void setDefaultAllowUserInteraction(boolean defaultallowuserinteraction) {
        throw new RuntimeException("skeleton method");
    }

    public static boolean getDefaultAllowUserInteraction() {
        throw new RuntimeException("skeleton method");
    }

    public void setUseCaches(boolean usecaches) {
        throw new RuntimeException("skeleton method");
    }

    public boolean getUseCaches(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setIfModifiedSince(long ifmodifiedsince) {
        throw new RuntimeException("skeleton method");
    }

    public long getIfModifiedSince(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public boolean getDefaultUseCaches(@Readonly URLConnection this)  {
        throw new RuntimeException("skeleton method");
    }

    public void setDefaultUseCaches(boolean defaultusecaches) {
        throw new RuntimeException("skeleton method");
    }

    public void setRequestProperty(String key, String value) {
        throw new RuntimeException("skeleton method");
    }

    public void addRequestProperty(String key, String value) {
        throw new RuntimeException("skeleton method");
    }

    public String getRequestProperty(@Readonly URLConnection this, String key)  {
        throw new RuntimeException("skeleton method");
    }

    public @Readonly Map<String, @Readonly List<String>> getRequestProperties() {
        throw new RuntimeException("skeleton method");
    }

    @Deprecated
    public static void setDefaultRequestProperty(String key, String value) {
        throw new RuntimeException("skeleton method");
    }

    @Deprecated
    public static String getDefaultRequestProperty(String key) {
        throw new RuntimeException("skeleton method");
    }

    public static synchronized void setContentHandlerFactory(@Readonly ContentHandlerFactory fac) {
        throw new RuntimeException("skeleton method");
    }

    synchronized ContentHandler getContentHandler() throws UnknownServiceException {
        throw new RuntimeException("skeleton method");
    }

    public static String guessContentTypeFromName(String fname) {
        throw new RuntimeException("skeleton method");
    }

    static public String guessContentTypeFromStream(InputStream is) throws IOException {
        throw new RuntimeException("skeleton method");
    }
}
