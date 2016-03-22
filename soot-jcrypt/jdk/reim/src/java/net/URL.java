package java.net;
import checkers.inference.reim.quals.*;

import java.io.IOException;
import java.io.InputStream;

public final class URL implements java.io.Serializable {
    static final long serialVersionUID = -7627629688361524110L;

    public URL(String protocol, String host, int port, String file) throws MalformedURLException {
        throw new RuntimeException("skeleton method"); 
    }

    public URL(String protocol, String host, String file) throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }

    public URL(String protocol, String host, int port, String file,
               @Readonly URLStreamHandler handler) throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }

    public URL(String spec) throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }

    public URL(URL context, String spec) throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }

    public URL(URL context, String spec, @Readonly URLStreamHandler handler) throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getQuery()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getPath()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getUserInfo()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getAuthority()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int getPort()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int getDefaultPort()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getProtocol()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getHost()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getFile()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getRef()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean equals(@Readonly Object obj)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized int hashCode()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public boolean sameFile(@Readonly URL other)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String toString()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String toExternalForm()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public URI toURI()  throws URISyntaxException {
        throw new RuntimeException("skeleton method");
    }

    public URLConnection openConnection() throws java.io.IOException {
        throw new RuntimeException("skeleton method");
    }

    public URLConnection openConnection(Proxy proxy) throws java.io.IOException {
        throw new RuntimeException("skeleton method");
    }

    public final InputStream openStream() throws java.io.IOException {
        throw new RuntimeException("skeleton method");
    }

    public final Object getContent() throws java.io.IOException {
        throw new RuntimeException("skeleton method");
    }

    public final Object getContent(Class[] classes) throws java.io.IOException {
        throw new RuntimeException("skeleton method");
    }

    public static void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
        throw new RuntimeException("skeleton method");
    }
}
