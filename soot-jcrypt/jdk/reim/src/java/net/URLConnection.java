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

    @ReadonlyThis public int getConnectTimeout()  {
        throw new RuntimeException("skeleton method");
    }

    public void setReadTimeout(int timeout) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int getReadTimeout()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis protected URLConnection(@Readonly URL url)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public URL getURL()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int getContentLength()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getContentLengthLong()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getContentType()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getContentEncoding()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getExpiration()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getDate()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getLastModified()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getHeaderField(String name)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public @Readonly Map<String, List<String>> getHeaderFields()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int getHeaderFieldInt(String name, int Default)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getHeaderFieldLong(String name, long Default)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getHeaderFieldDate(String name, long Default)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getHeaderFieldKey(int n)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getHeaderField(int n)  {
        throw new RuntimeException("skeleton method");
    }

    public Object getContent() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public Object getContent(Class[] classes) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public Permission getPermission()  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public InputStream getInputStream() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public OutputStream getOutputStream() throws IOException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String toString()  {
        throw new RuntimeException("skeleton method");
    }

    public void setDoInput(boolean doinput) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean getDoInput()  {
        throw new RuntimeException("skeleton method");
    }

    public void setDoOutput(boolean dooutput) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean getDoOutput()  {
        throw new RuntimeException("skeleton method");
    }

    public void setAllowUserInteraction(boolean allowuserinteraction) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean getAllowUserInteraction()  {
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

    @ReadonlyThis public boolean getUseCaches()  {
        throw new RuntimeException("skeleton method");
    }

    public void setIfModifiedSince(long ifmodifiedsince) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public long getIfModifiedSince()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean getDefaultUseCaches()  {
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

    @ReadonlyThis public String getRequestProperty(String key)  {
        throw new RuntimeException("skeleton method");
    }

    public @Readonly Map<String, List<String>> getRequestProperties() {
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
