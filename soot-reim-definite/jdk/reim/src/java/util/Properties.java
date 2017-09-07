package java.util;
import checkers.inference.reim.quals.*;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class Properties extends Hashtable<Object,Object> {
    private static final long serialVersionUID = 4112578634029874840L;

    protected Properties defaults;

    public Properties() {
        throw new RuntimeException("skeleton method");
    }

    public Properties(Properties defaults) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized Object setProperty(String key, String value) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized void load(Reader reader) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public synchronized void load(InputStream inStream) throws IOException {
        throw new RuntimeException("skeleton method");
    }

    @Deprecated
    @ReadonlyThis public synchronized void save( OutputStream out, String comments)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public void store( Writer writer, String comments)  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public void store( OutputStream out, String comments)  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public synchronized void loadFromXML(InputStream in)
        throws IOException, InvalidPropertiesFormatException
    {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized void storeToXML( OutputStream os, String comment)  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized void storeToXML( OutputStream os, String comment, String encoding) 
        throws IOException
    {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getProperty( String key)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public String getProperty( String key, String defaultValue)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public Enumeration<?> propertyNames()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public Set<String> stringPropertyNames()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public void list( PrintStream out)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public void list( PrintWriter out)  {
        throw new RuntimeException("skeleton method");
    }
}
