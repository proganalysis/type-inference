package javax.servlet;

import checkers.inference.sflow.quals.Tainted;
import checkers.inference.sflow.quals.Poly;
import checkers.inference.reim.quals.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;

public interface ServletRequest {
    Object getAttribute(/*>>> @Readonly ServletRequest this,*/ String arg0);
    Enumeration<String> getAttributeNames(/*>>> @Readonly ServletRequest this*/);
    String getCharacterEncoding(/*>>> @Readonly ServletRequest this*/);
    void setCharacterEncoding(String arg0) throws UnsupportedEncodingException;
    int getContentLength(/*>>> @Readonly ServletRequest this*/);
    String getContentType(/*>>> @Readonly ServletRequest this*/);
    ServletInputStream getInputStream(/*>>> @Readonly ServletRequest this*/) throws IOException;
    /*-@Tainted*/ String getParameter(/*>>> @Readonly ServletRequest this,*/ String arg0);
    /*-@Tainted*/ Enumeration<String> getParameterNames(/*>>> @Readonly ServletRequest this*/ );
    /*-@Tainted*/ /*@Poly*/ String /*-@Tainted*/ [] getParameterValues(/*>>> @Readonly ServletRequest this,*/ String arg0);
    /*-@Tainted*/ Map<String,String[]> getParameterMap(/*>>> @Readonly ServletRequest this*/ );
    /*-@Tainted*/ String getProtocol(/*>>> @Readonly ServletRequest this*/ );
    String getScheme(/*>>> @Readonly ServletRequest this*/ );
    String getServerName(/*>>> @Readonly ServletRequest this*/ );
    int getServerPort(/*>>> @Readonly ServletRequest this*/ );
    BufferedReader getReader(/*>>> @Readonly ServletRequest this*/ ) throws IOException;
    String getRemoteAddr(/*>>> @Readonly ServletRequest this*/ );
    String getRemoteHost(/*>>> @Readonly ServletRequest this*/ );
    void setAttribute(String arg0, Object arg1);
    void removeAttribute(String arg0);
    Locale getLocale(/*>>> @Readonly ServletRequest this*/ );
    Enumeration<Locale> getLocales(/*>>> @Readonly ServletRequest this*/ );
    boolean isSecure(/*>>> @Readonly ServletRequest this*/ );
    RequestDispatcher getRequestDispatcher(/*>>> @Readonly ServletRequest this,*/ String arg0);
    String getRealPath(/*>>> @Readonly ServletRequest this,*/ String arg0);
    int getRemotePort(/*>>> @Readonly ServletRequest this*/ );
    String getLocalName(/*>>> @Readonly ServletRequest this*/ );
    String getLocalAddr(/*>>> @Readonly ServletRequest this*/ );
    int getLocalPort(/*>>> @Readonly ServletRequest this*/ );
    ServletContext getServletContext(/*>>> @Readonly ServletRequest this*/ );
    AsyncContext startAsync() throws IllegalStateException;
    AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException;
    boolean isAsyncStarted(/*>>> @Readonly ServletRequest this*/ );
    boolean isAsyncSupported(/*>>> @Readonly ServletRequest this*/ );
    AsyncContext getAsyncContext(/*>>> @Readonly ServletRequest this*/ );
    DispatcherType getDispatcherType(/*>>> @Readonly ServletRequest this*/ );
}
