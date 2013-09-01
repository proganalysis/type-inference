package javax.servlet;

import checkers.inference.sflow.quals.Tainted;
import java.io.*;
import java.sql.*;
import java.util.*;

public class ServletOutputStream extends OutputStream {
    protected ServletOutputStream() { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ String arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ boolean arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ char arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ int arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ long arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ float arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void print(/*@Tainted*/ double arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println() throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ String arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ boolean arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ char arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ int arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ long arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ float arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void println(/*@Tainted*/ double arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void write(int arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void write(byte[] arg0) throws IOException { throw new RuntimeException("skeleton method"); }
    public void write(byte[] arg0, int arg1, int arg2) throws IOException { throw new RuntimeException("skeleton method"); }
    public void flush() throws IOException { throw new RuntimeException("skeleton method"); }
    public void close() throws IOException { throw new RuntimeException("skeleton method"); }
}
