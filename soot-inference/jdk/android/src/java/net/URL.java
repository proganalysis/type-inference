package java.net;

import checkers.inference.reim.quals.*;
import checkers.inference.sflow.quals.*;


public final class URL implements java.io.Serializable {
    static final long serialVersionUID = 0;
    private static final java.lang.String protocolPathProp = null;
    private java.lang.String protocol;
    private java.lang.String host;
    private int port;
    private java.lang.String file;
    private transient java.lang.String query;
    private java.lang.String authority;
    private transient java.lang.String path;
    private transient java.lang.String userInfo;
    private java.lang.String ref;
    transient java.net.InetAddress hostAddress;
    transient java.net.URLStreamHandler handler;
    private int hashCode;
    static java.net.URLStreamHandlerFactory factory;
    static java.util.Hashtable handlers;
    private static java.lang.Object streamHandlerLock;
    private static final java.lang.String GOPHER = null;
    private static final java.lang.String ENABLE_GOPHER_PROP = null;
    private static final boolean enableGopher = false;
    private static final java.lang.String JDK_PACKAGE_PREFIX = null;
    public URL(java.lang.String arg0, @Safe java.lang.String arg1, int arg2, java.lang.String arg3) throws java.net.MalformedURLException { throw new RuntimeException("skeleton method"); }
    public URL(java.lang.String arg0, @Safe java.lang.String arg1, java.lang.String arg2) throws java.net.MalformedURLException { throw new RuntimeException("skeleton method"); }
    public URL(java.lang.String arg0, @Safe java.lang.String arg1, int arg2, java.lang.String arg3, java.net.URLStreamHandler arg4) throws java.net.MalformedURLException { throw new RuntimeException("skeleton method"); }
    public URL(@Safe java.lang.String arg0) throws java.net.MalformedURLException { throw new RuntimeException("skeleton method"); }
    public URL(java.net.URL arg0, @Safe java.lang.String arg1) throws java.net.MalformedURLException { throw new RuntimeException("skeleton method"); }
    public URL(java.net.URL arg0, @Safe java.lang.String arg1, java.net.URLStreamHandler arg2) throws java.net.MalformedURLException { throw new RuntimeException("skeleton method"); }
    private boolean isValidProtocol(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    private void checkSpecifyHandler(java.lang.SecurityManager arg0) { throw new RuntimeException("skeleton method"); }
    protected void set(java.lang.String arg0, java.lang.String arg1, int arg2, java.lang.String arg3, java.lang.String arg4) { throw new RuntimeException("skeleton method"); }
    protected void set(java.lang.String arg0, java.lang.String arg1, int arg2, java.lang.String arg3, java.lang.String arg4, java.lang.String arg5, java.lang.String arg6, java.lang.String arg7) { throw new RuntimeException("skeleton method"); }
    public java.lang.String getQuery() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getPath() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getUserInfo() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getAuthority() { throw new RuntimeException("skeleton method"); }
    public int getPort() { throw new RuntimeException("skeleton method"); }
    public int getDefaultPort() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getProtocol() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getHost() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getFile() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getRef() { throw new RuntimeException("skeleton method"); }
    public boolean equals(java.lang.Object arg0) { throw new RuntimeException("skeleton method"); }
    public synchronized int hashCode() { throw new RuntimeException("skeleton method"); }
    public boolean sameFile(java.net.URL arg0) { throw new RuntimeException("skeleton method"); }
    public java.lang.String toString() { throw new RuntimeException("skeleton method"); }
    public java.lang.String toExternalForm() { throw new RuntimeException("skeleton method"); }
    public java.net.URI toURI() throws java.net.URISyntaxException { throw new RuntimeException("skeleton method"); }
    public java.net.URLConnection openConnection() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public @Safe java.net.URLConnection openConnection(java.net.Proxy arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public final java.io.InputStream openStream() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public final java.lang.Object getContent() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public final java.lang.Object getContent(java.lang.Class[] arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public static void setURLStreamHandlerFactory(java.net.URLStreamHandlerFactory arg0) { throw new RuntimeException("skeleton method"); }
    static java.net.URLStreamHandler getURLStreamHandler(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    private synchronized void writeObject(java.io.ObjectOutputStream arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private synchronized void readObject(java.io.ObjectInputStream arg0) throws java.io.IOException,java.lang.ClassNotFoundException { throw new RuntimeException("skeleton method"); }
}
