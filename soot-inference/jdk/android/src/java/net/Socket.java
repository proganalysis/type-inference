package java.net;

import checkers.inference.reim.quals.*;
import checkers.inference.sflow.quals.*;


public class Socket implements java.io.Closeable {
    private boolean created;
    private boolean bound;
    private boolean connected;
    private boolean closed;
    private java.lang.Object closeLock;
    private boolean shutIn;
    private boolean shutOut;
    java.net.SocketImpl impl;
    private boolean oldImpl;
    private static java.net.SocketImplFactory factory;
    public Socket() { throw new RuntimeException("skeleton method"); }
    public Socket(java.net.Proxy arg0) { throw new RuntimeException("skeleton method"); }
    protected Socket(java.net.SocketImpl arg0) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public Socket(java.lang.String arg0, int arg1) throws java.net.UnknownHostException,java.io.IOException { throw new RuntimeException("skeleton method"); }
    public Socket(java.net.InetAddress arg0, int arg1) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public Socket(java.lang.String arg0, int arg1, java.net.InetAddress arg2, int arg3) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public Socket(java.net.InetAddress arg0, int arg1, java.net.InetAddress arg2, int arg3) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public Socket(java.lang.String arg0, int arg1, boolean arg2) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public Socket(java.net.InetAddress arg0, int arg1, boolean arg2) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private Socket(java.net.SocketAddress arg0, java.net.SocketAddress arg1, boolean arg2) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    void createImpl(boolean arg0) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    private void checkOldImpl() { throw new RuntimeException("skeleton method"); }
    void setImpl() { throw new RuntimeException("skeleton method"); }
    java.net.SocketImpl getImpl() throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public void connect(java.net.SocketAddress arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void connect(java.net.SocketAddress arg0, int arg1) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void bind(java.net.SocketAddress arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void checkAddress(java.net.InetAddress arg0, java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    final void postAccept() { throw new RuntimeException("skeleton method"); }
    void setCreated() { throw new RuntimeException("skeleton method"); }
    void setBound() { throw new RuntimeException("skeleton method"); }
    void setConnected() { throw new RuntimeException("skeleton method"); }
    public java.net.InetAddress getInetAddress() { throw new RuntimeException("skeleton method"); }
    public java.net.InetAddress getLocalAddress() { throw new RuntimeException("skeleton method"); }
    public int getPort() { throw new RuntimeException("skeleton method"); }
    public int getLocalPort() { throw new RuntimeException("skeleton method"); }
    public java.net.SocketAddress getRemoteSocketAddress() { throw new RuntimeException("skeleton method"); }
    public java.net.SocketAddress getLocalSocketAddress() { throw new RuntimeException("skeleton method"); }
    public java.nio.channels.SocketChannel getChannel() { throw new RuntimeException("skeleton method"); }
    public java.io.InputStream getInputStream() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public @Safe java.io.OutputStream getOutputStream() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void setTcpNoDelay(boolean arg0) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public boolean getTcpNoDelay() throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public void setSoLinger(boolean arg0, int arg1) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public int getSoLinger() throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public void sendUrgentData(int arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void setOOBInline(boolean arg0) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public boolean getOOBInline() throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public synchronized void setSoTimeout(int arg0) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public synchronized int getSoTimeout() throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public synchronized void setSendBufferSize(int arg0) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public synchronized int getSendBufferSize() throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public synchronized void setReceiveBufferSize(int arg0) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public synchronized int getReceiveBufferSize() throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public void setKeepAlive(boolean arg0) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public boolean getKeepAlive() throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public void setTrafficClass(int arg0) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public int getTrafficClass() throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public void setReuseAddress(boolean arg0) throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public boolean getReuseAddress() throws java.net.SocketException { throw new RuntimeException("skeleton method"); }
    public synchronized void close() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void shutdownInput() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void shutdownOutput() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public java.lang.String toString() { throw new RuntimeException("skeleton method"); }
    public boolean isConnected() { throw new RuntimeException("skeleton method"); }
    public boolean isBound() { throw new RuntimeException("skeleton method"); }
    public boolean isClosed() { throw new RuntimeException("skeleton method"); }
    public boolean isInputShutdown() { throw new RuntimeException("skeleton method"); }
    public boolean isOutputShutdown() { throw new RuntimeException("skeleton method"); }
    public static synchronized void setSocketImplFactory(java.net.SocketImplFactory arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void setPerformancePreferences(int arg0, int arg1, int arg2) { throw new RuntimeException("skeleton method"); }
}
