package javax.servlet.jsp;

import checkers.inference.sflow.quals.Safe;
import checkers.inference.sflow.quals.Poly;
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
    public void print(/*@Safe*/ boolean arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Safe*/ char arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Safe*/ int arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Safe*/ long arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Safe*/ float arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Safe*/ double arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Poly*/ char /*@Safe*/ [] arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Safe*/ String arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Safe*/ Object arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println() throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Safe*/ boolean arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Safe*/ char arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Safe*/ int arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Safe*/ long arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Safe*/ float arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Safe*/ double arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Poly*/ char /*@Safe*/ [] arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Safe*/ String arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Safe*/ Object arg0) throws IOException { throw new RuntimeException("skeleton method"); }
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
