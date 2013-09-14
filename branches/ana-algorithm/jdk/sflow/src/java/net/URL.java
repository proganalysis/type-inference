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

    public String getQuery(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getPath(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getUserInfo(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getAuthority(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public int getPort(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public int getDefaultPort(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getProtocol(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getHost(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getFile(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getRef(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public boolean equals(@Readonly URL this, @Readonly Object obj)  {
        throw new RuntimeException("skeleton method");
    }

    public synchronized int hashCode(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public boolean sameFile(@Readonly URL this, @Readonly URL other)  {
        throw new RuntimeException("skeleton method");
    }

    public String toString(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String toExternalForm(@Readonly URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public URI toURI(@Readonly URL this)  throws URISyntaxException {
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
