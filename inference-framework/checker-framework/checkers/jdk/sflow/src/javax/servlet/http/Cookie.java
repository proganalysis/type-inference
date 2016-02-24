package javax.servlet.http;

import checkers.inference.sflow.quals.Tainted;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;

class Cookie implements Cloneable, Serializable {
    public Cookie(String arg0, String arg1) { throw new RuntimeException("skeleton method"); }
    public void setComment(String arg0) { throw new RuntimeException("skeleton method"); }
    public /*@Tainted*/ String getComment() { throw new RuntimeException("skeleton method"); }
    public void setDomain(String arg0) { throw new RuntimeException("skeleton method"); }
    public /*@Tainted*/ String getDomain() { throw new RuntimeException("skeleton method"); }
    public void setMaxAge(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getMaxAge() { throw new RuntimeException("skeleton method"); }
    public void setPath(String arg0) { throw new RuntimeException("skeleton method"); }
    public String getPath() { throw new RuntimeException("skeleton method"); }
    public void setSecure(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public boolean getSecure() { throw new RuntimeException("skeleton method"); }
    public /*@Tainted*/ String getName() { throw new RuntimeException("skeleton method"); }
    public void setValue(String arg0) { throw new RuntimeException("skeleton method"); }
    public /*@Tainted*/ String getValue() { throw new RuntimeException("skeleton method"); }
    public int getVersion() { throw new RuntimeException("skeleton method"); }
    public void setVersion(int arg0) { throw new RuntimeException("skeleton method"); }
    public Object clone() { throw new RuntimeException("skeleton method"); }
    public void setHttpOnly(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public boolean isHttpOnly() { throw new RuntimeException("skeleton method"); }
}
