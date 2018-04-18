package java.lang;
import checkers.inference.reim.quals.*;

public final class StringBuffer
    extends AbstractStringBuilder
    implements java.io.Serializable, CharSequence {

    static final long serialVersionUID = 3388685877147921107L;

    public StringBuffer() {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer(int capacity) {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer(String str) {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer(@Readonly CharSequence seq) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized int length()  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized int capacity()  {
        throw new RuntimeException("skeleton method");
    }

    public synchronized void ensureCapacity(int minimumCapacity) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized void trimToSize() {
        throw new RuntimeException("skeleton method");
    }

    public synchronized void setLength(int newLength) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized char charAt( int index)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized int codePointAt( int index)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized int codePointBefore( int index)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized int codePointCount( int beginIndex, int endIndex)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized int offsetByCodePoints( int index, int codePointOffset)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized void getChars( int srcBegin, int srcEnd, char dst[],
                                      int dstBegin) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized void setCharAt(int index, char ch) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(@Readonly Object obj) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(@Readonly String str) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(@Readonly StringBuffer sb) {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer append(@Readonly CharSequence s) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(@Readonly CharSequence s, int start, int end) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(@Readonly char[] str) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(@Readonly char[] str, int offset, int len) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(boolean b) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(char c) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(int i) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer appendCodePoint(int codePoint) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(long lng) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(float f) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer append(double d) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer delete(int start, int end) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer deleteCharAt(int index) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer replace(int start, int end,  String str) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized String substring( int start)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized @Readonly CharSequence subSequence( int start, int end)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized String substring( int start, int end)  {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer insert(int index, @Readonly char[] str, int offset, int len) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer insert(int offset, @Readonly Object obj) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer insert(int offset, String str) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized  StringBuffer insert(int offset, @Readonly char[] str) {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer insert(int dstOffset, @Readonly CharSequence s) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer insert(int dstOffset, @Readonly CharSequence s, int start, int end) {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer insert(int offset, boolean b) {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer insert(int offset, char c) {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer insert(int offset, int i) {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer insert(int offset, long l) {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer insert(int offset, float f) {
        throw new RuntimeException("skeleton method");
    }

    public StringBuffer insert(int offset, double d) {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int indexOf( String str)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized int indexOf( String str, int fromIndex)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public int lastIndexOf(  String str)  {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized int lastIndexOf( String str, int fromIndex)  {
        throw new RuntimeException("skeleton method");
    }

    public synchronized StringBuffer reverse() {
        throw new RuntimeException("skeleton method");
    }

    @ReadonlyThis public synchronized String toString()  {
        throw new RuntimeException("skeleton method");
    }

    private static final java.io.ObjectStreamField[] serialPersistentFields =
    {
        new java.io.ObjectStreamField("value", char[].class),
        new java.io.ObjectStreamField("count", Integer.TYPE),
        new java.io.ObjectStreamField("shared", Boolean.TYPE),
    };

    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        throw new RuntimeException("skeleton method");
    }

    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        throw new RuntimeException("skeleton method");
    }
}
