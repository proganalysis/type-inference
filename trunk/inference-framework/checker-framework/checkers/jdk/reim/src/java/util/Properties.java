package java.util;
import checkers.inference2.reimN.quals.*;

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
    public synchronized void save(@ReadRead Properties this, OutputStream out, String comments)  {
        throw new RuntimeException("skeleton method");
    }

    public void store(@ReadRead Properties this, Writer writer, String comments)  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public void store(@ReadRead Properties this, OutputStream out, String comments)  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public synchronized void loadFromXML(InputStream in)
        throws IOException, InvalidPropertiesFormatException
    {
        throw new RuntimeException("skeleton method");
    }

    public synchronized void storeToXML(@ReadRead Properties this, OutputStream os, String comment)  throws IOException {
        throw new RuntimeException("skeleton method");
    }

    public synchronized void storeToXML(@ReadRead Properties this, OutputStream os, String comment, String encoding) 
        throws IOException
    {
        throw new RuntimeException("skeleton method");
    }

    public String getProperty(@ReadRead Properties this, String key)  {
        throw new RuntimeException("skeleton method");
    }

    public String getProperty(@ReadRead Properties this, String key, String defaultValue)  {
        throw new RuntimeException("skeleton method");
    }

    public Enumeration<?> propertyNames(@ReadRead Properties this)  {
        throw new RuntimeException("skeleton method");
    }

    public Set<String> stringPropertyNames(@ReadRead Properties this)  {
        throw new RuntimeException("skeleton method");
    }

    public void list(@ReadRead Properties this, PrintStream out)  {
        throw new RuntimeException("skeleton method");
    }

    public void list(@ReadRead Properties this, PrintWriter out)  {
        throw new RuntimeException("skeleton method");
    }
}
