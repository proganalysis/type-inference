package javax.servlet.jsp;

import checkers.inference.reimflow.quals.Tainted;
import checkers.inference.reimflow.quals.Poly;
import java.io.*;
import java.sql.*;
import java.util.*;

public class JspWriter extends Writer {
    int NO_BUFFER;
    int DEFAULT_BUFFER;
    int UNBOUNDED_BUFFER;
    protected int bufferSize;
    protected boolean autoFlush;
    protected JspWriter(int arg0, boolean arg1) { throw new RuntimeException("skeleton method"); }
    public void newLine() throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ boolean arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ char arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ int arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ long arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ float arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ double arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Poly*/ char /*@Tainted*/ [] arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ String arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ Object arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println() throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ boolean arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ char arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ int arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ long arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ float arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ double arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Poly*/ char /*@Tainted*/ [] arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ String arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ Object arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void clear() throws IOException { throw new RuntimeException("skeleton method"); }
    public void clearBuffer() throws IOException { throw new RuntimeException("skeleton method"); }
    public int getBufferSize() { throw new RuntimeException("skeleton method"); }
    public int getRemaining() { throw new RuntimeException("skeleton method"); }
    public boolean isAutoFlush() { throw new RuntimeException("skeleton method"); }
    public void write(int arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void write(char[] arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void write(char[] arg0, int arg1, int arg2) throws IOException { throw new RuntimeException("skeleton method"); }
    public void write(String arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void write(String arg0, int arg1, int arg2) throws IOException { throw new RuntimeException("skeleton method"); }
    public Writer append(CharSequence arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public Writer append(CharSequence arg0, int arg1, int arg2) throws IOException { throw new RuntimeException("skeleton method"); }
    public Writer append(char arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void flush() throws IOException { throw new RuntimeException("skeleton method"); }
    public void close() throws IOException { throw new RuntimeException("skeleton method"); }
}
