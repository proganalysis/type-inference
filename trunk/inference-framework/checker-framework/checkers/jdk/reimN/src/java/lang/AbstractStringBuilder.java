package java.lang;
import checkers.inference2.reimN.quals.*;

import java.util.Arrays;

abstract class AbstractStringBuilder implements Appendable, CharSequence {
    char value[];
    int count;
    AbstractStringBuilder() {
        throw new RuntimeException("skeleton method");
    }

    AbstractStringBuilder(int capacity) {
        throw new RuntimeException("skeleton method");
    }

    public int length(@ReadRead AbstractStringBuilder this)  {
        throw new RuntimeException("skeleton method");
    }

    public int capacity(@ReadRead AbstractStringBuilder this)  {
        throw new RuntimeException("skeleton method");
    }

    public void ensureCapacity(int minimumCapacity) {
        throw new RuntimeException("skeleton method");
    }

    void expandCapacity(int minimumCapacity) {
        throw new RuntimeException("skeleton method");
    }

    public void trimToSize() {
        throw new RuntimeException("skeleton method");
    }

    public void setLength(int newLength) {
        throw new RuntimeException("skeleton method");
    }

    public char charAt(@ReadRead AbstractStringBuilder this, int index)  {
        throw new RuntimeException("skeleton method");
    }

    public int codePointAt(@ReadRead AbstractStringBuilder this, int index)  {
        throw new RuntimeException("skeleton method");
    }

    public int codePointBefore(@ReadRead AbstractStringBuilder this, int index)  {
        throw new RuntimeException("skeleton method");
    }

    public int codePointCount(@ReadRead AbstractStringBuilder this, int beginIndex, int endIndex)  {
        throw new RuntimeException("skeleton method");
    }

    public int offsetByCodePoints(@ReadRead AbstractStringBuilder this, int index, int codePointOffset)  {
        throw new RuntimeException("skeleton method");
    }

    public void getChars(@ReadRead AbstractStringBuilder this, int srcBegin, int srcEnd, char dst[],
                                      int dstBegin){
        throw new RuntimeException("skeleton method");
    }

    public void setCharAt(int index, char ch) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(@ReadRead Object obj) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(String str) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(@ReadRead StringBuffer sb) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(@ReadRead CharSequence s) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(@ReadRead CharSequence s, int start, int end) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(char str[]) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(char str[], int offset, int len) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(boolean b) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(char c) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(int i) {
        throw new RuntimeException("skeleton method");
    }

    final static int [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
                                     99999999, 999999999, Integer.MAX_VALUE };

    static int stringSizeOfInt(int x) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(long l) {
        throw new RuntimeException("skeleton method");
    }

    static int stringSizeOfLong(long x) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(float f) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder append(double d) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder delete(int start, int end) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder appendCodePoint(int codePoint) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder deleteCharAt(int index) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder replace(int start, int end, String str) {
        throw new RuntimeException("skeleton method");
    }

    public String substring(@ReadRead AbstractStringBuilder this, int start)  {
        throw new RuntimeException("skeleton method");
    }

    public @ReadRead CharSequence subSequence(@ReadRead AbstractStringBuilder this, int start, int end)  {
        throw new RuntimeException("skeleton method");
    }

    public String substring(@ReadRead AbstractStringBuilder this, int start, int end)  {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int index, char str[], int offset,
                                        int len) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int offset, @ReadRead Object obj) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int offset, String str) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int offset, char str[]) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int dstOffset, @ReadRead CharSequence s) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int dstOffset, @ReadRead CharSequence s,
                                           int start, int end) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int offset, boolean b) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int offset, char c) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int offset, int i) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int offset, long l) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int offset, float f) {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder insert(int offset, double d) {
        throw new RuntimeException("skeleton method");
    }

    public int indexOf(@ReadRead AbstractStringBuilder this, String str)  {
        throw new RuntimeException("skeleton method");
    }

    public int indexOf(@ReadRead AbstractStringBuilder this, String str, int fromIndex)  {
        throw new RuntimeException("skeleton method");
    }

    public int lastIndexOf(@ReadRead AbstractStringBuilder this, String str)  {
        throw new RuntimeException("skeleton method");
    }

    public int lastIndexOf(@ReadRead AbstractStringBuilder this, String str, int fromIndex)  {
        throw new RuntimeException("skeleton method");
    }

    public AbstractStringBuilder reverse() {
        throw new RuntimeException("skeleton method");
    }

    public abstract String toString(@ReadRead AbstractStringBuilder this) ;

    final char[] getValue() {
        throw new RuntimeException("skeleton method");
    }

}
