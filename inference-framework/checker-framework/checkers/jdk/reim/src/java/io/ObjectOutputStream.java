package java.io;
import checkers.inference.reim.quals.*;

public class ObjectOutputStream extends java.io.OutputStream implements java.io.ObjectOutput, java.io.ObjectStreamConstants {
    public ObjectOutputStream(java.io.OutputStream arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    protected ObjectOutputStream() throws java.io.IOException,java.lang.SecurityException { throw new RuntimeException("skeleton method"); }
    public void useProtocolVersion(int arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public final void writeObject(@Readonly java.lang.Object arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    protected void writeObjectOverride(java.lang.Object arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeUnshared(java.lang.Object arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void defaultWriteObject() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeFields() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void reset() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    protected void annotateClass(java.lang.Class<?> arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    protected void annotateProxyClass(java.lang.Class<?> arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    protected java.lang.Object replaceObject(java.lang.Object arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    protected boolean enableReplaceObject(boolean arg0) throws java.lang.SecurityException { throw new RuntimeException("skeleton method"); }
    protected void writeStreamHeader() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    protected void writeClassDescriptor(java.io.ObjectStreamClass arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void write(int arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void write(byte[] arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void write(byte[] arg0, int arg1, int arg2) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void flush() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    protected void drain() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void close() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeBoolean(boolean arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeByte(int arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeShort(int arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeChar(int arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeInt(int arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeLong(long arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeFloat(float arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeDouble(double arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeBytes(java.lang.String arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeChars(java.lang.String arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    public void writeUTF(java.lang.String arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    int getProtocolVersion() { throw new RuntimeException("skeleton method"); }
    void writeTypeString(java.lang.String arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void verifySubclass() { throw new RuntimeException("skeleton method"); }
    private static boolean auditSubclass(java.lang.Class arg0) { throw new RuntimeException("skeleton method"); }
    private void clear() { throw new RuntimeException("skeleton method"); }
    private void writeObject0(java.lang.Object arg0, boolean arg1) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeNull() throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeHandle(int arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeClass(java.lang.Class arg0, boolean arg1) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeClassDesc(java.io.ObjectStreamClass arg0, boolean arg1) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private boolean isCustomSubclass() { throw new RuntimeException("skeleton method"); }
    private void writeProxyDesc(java.io.ObjectStreamClass arg0, boolean arg1) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeNonProxyDesc(java.io.ObjectStreamClass arg0, boolean arg1) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeString(java.lang.String arg0, boolean arg1) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeArray(java.lang.Object arg0, java.io.ObjectStreamClass arg1, boolean arg2) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeEnum(java.lang.Enum arg0, java.io.ObjectStreamClass arg1, boolean arg2) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeOrdinaryObject(java.lang.Object arg0, java.io.ObjectStreamClass arg1, boolean arg2) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeExternalData(java.io.Externalizable arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeSerialData(java.lang.Object arg0, java.io.ObjectStreamClass arg1) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void defaultWriteFields(java.lang.Object arg0, java.io.ObjectStreamClass arg1) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
    private void writeFatalException(java.io.IOException arg0) throws java.io.IOException { throw new RuntimeException("skeleton method"); }
}
