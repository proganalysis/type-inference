package java.net;
import checkers.inference2.reimN.quals.*;

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
               @ReadRead URLStreamHandler handler) throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }

    public URL(String spec) throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }

    public URL(URL context, String spec) throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }

    public URL(URL context, String spec, @ReadRead URLStreamHandler handler) throws MalformedURLException {
        throw new RuntimeException("skeleton method");
    }

    public String getQuery(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getPath(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getUserInfo(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getAuthority(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public int getPort(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public int getDefaultPort(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getProtocol(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getHost(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getFile(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String getRef(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public boolean equals(@ReadRead URL this, @ReadRead Object obj)  {
        throw new RuntimeException("skeleton method");
    }

    public synchronized int hashCode(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public boolean sameFile(@ReadRead URL this, @ReadRead URL other)  {
        throw new RuntimeException("skeleton method");
    }

    public String toString(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public String toExternalForm(@ReadRead URL this)  {
        throw new RuntimeException("skeleton method");
    }

    public URI toURI(@ReadRead URL this)  throws URISyntaxException {
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
