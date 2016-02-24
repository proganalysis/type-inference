package javax.servlet.http;

import checkers.inference.sflow.quals.Safe;
import checkers.inference.reim.quals.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;

public interface HttpServletResponse extends ServletResponse {
    void addCookie(/*@Readonly*/ Cookie arg0);
    boolean containsHeader(String arg0);
    String encodeURL(String arg0);
    String encodeRedirectURL(String arg0);
    String encodeUrl(String arg0);
    String encodeRedirectUrl(String arg0);
    void sendError(/*@Safe*/ int arg0, String arg1) throws IOException;
    void sendError(/*@Safe*/ int arg0) throws IOException;
    void sendRedirect(/*@Safe*/ String arg0) throws IOException;
    void setDateHeader(String arg0, long arg1);
    void addDateHeader(String arg0, long arg1);
    void setHeader(/*@Safe*/ String arg0, /*@Safe*/ String arg1);
    void addHeader(String arg0, String arg1);
    void setIntHeader(String arg0, int arg1);
    void addIntHeader(String arg0, int arg1);
    void setStatus(int arg0);
    void setStatus(int arg0, String arg1);
    int getStatus();
    String getHeader(String arg0);
    Collection<String> getHeaders(String arg0);
    Collection<String> getHeaderNames();
}
