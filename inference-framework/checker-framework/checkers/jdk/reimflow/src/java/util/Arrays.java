package java.util;
import checkers.inference.reim.quals.*;

import java.lang.reflect.*;

public class Arrays {
    private Arrays() {}

    public static void sort(long[] a) { throw new RuntimeException("skeleton method"); }
    public static void sort(long[] a, int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public static void sort(int[] a) { throw new RuntimeException("skeleton method"); }
    public static void sort(int[] a, int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public static void sort(short[] a) { throw new RuntimeException("skeleton method"); }
    public static void sort(short[] a, int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public static void sort(char[] a) { throw new RuntimeException("skeleton method"); }
    public static void sort(char[] a, int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public static void sort(byte[] a) { throw new RuntimeException("skeleton method"); }
    public static void sort(byte[] a, int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public static void sort(double[] a) { throw new RuntimeException("skeleton method"); }
    public static void sort(double[] a, int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public static void sort(float[] a) { throw new RuntimeException("skeleton method"); }
    public static void sort(float[] a, int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public static void sort(@Readonly Object[] a) { throw new RuntimeException("skeleton method"); }
    public static void sort(@Readonly Object[] a, int fromIndex, int toIndex) { throw new RuntimeException("skeleton method"); }
    public static <T> void sort(@Readonly T[] a, Comparator<? super T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> void sort(@Readonly T[] a, int fromIndex, int toIndex, Comparator<? super T> c) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(long @Readonly [] a, long key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(long @Readonly [] a, int fromIndex, int toIndex, long key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(int @Readonly [] a, int key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(int @Readonly [] a, int fromIndex, int toIndex, int key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(short @Readonly [] a, short key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(short @Readonly [] a, int fromIndex, int toIndex, short key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(char @Readonly [] a, char key ) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(char @Readonly [] a, int fromIndex, int toIndex, char key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(byte @Readonly [] a, byte key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(byte @Readonly [] a, int fromIndex, int toIndex, byte key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(double @Readonly [] a, double key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(double @Readonly [] a, int fromIndex, int toIndex, double key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(float @Readonly [] a, float key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(float @Readonly [] a, int fromIndex, int toIndex, float key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly Object @Readonly [] a, @Readonly Object key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly Object @Readonly [] a, int fromIndex, int toIndex, @Readonly Object key) { throw new RuntimeException("skeleton method"); }
    public static <T> int binarySearch(T @Readonly [] a, @Readonly T key,  Comparator<? super T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> int binarySearch(T @Readonly [] a, int fromIndex, int toIndex, @Readonly T key, Comparator<? super T> c) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(long @Readonly [] a, long @Readonly [] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(int @Readonly [] a, int @Readonly [] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(short @Readonly [] a, short @Readonly [] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(char @Readonly [] a, char @Readonly [] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(byte @Readonly [] a, byte @Readonly [] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(boolean @Readonly [] a, boolean @Readonly [] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(double @Readonly [] a, double @Readonly [] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(float @Readonly [] a, float @Readonly [] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(@Readonly Object @Readonly [] a, @Readonly Object @Readonly [] a2) { throw new RuntimeException("skeleton method"); }
    public static void fill(long[] a, long val) { throw new RuntimeException("skeleton method"); }
    public static void fill(long[] a, int fromIndex, int toIndex, long val) { throw new RuntimeException("skeleton method"); }
    public static void fill(int[] a, int val) { throw new RuntimeException("skeleton method"); }
    public static void fill(int[] a, int fromIndex, int toIndex, int val) { throw new RuntimeException("skeleton method"); }
    public static void fill(short[] a, short val) { throw new RuntimeException("skeleton method"); }
    public static void fill(short[] a, int fromIndex, int toIndex, short val) { throw new RuntimeException("skeleton method"); }
    public static void fill(char[] a, char val) { throw new RuntimeException("skeleton method"); }
    public static void fill(char[] a, int fromIndex, int toIndex, char val) { throw new RuntimeException("skeleton method"); }
    public static void fill(byte[] a, byte val) { throw new RuntimeException("skeleton method"); }
    public static void fill(byte[] a, int fromIndex, int toIndex, byte val) { throw new RuntimeException("skeleton method"); }
    public static void fill(boolean[] a, boolean val) { throw new RuntimeException("skeleton method"); }
    public static void fill(boolean[] a, int fromIndex, int toIndex, boolean val) { throw new RuntimeException("skeleton method"); }
    public static void fill(double[] a, double val) { throw new RuntimeException("skeleton method"); }
    public static void fill(double[] a, int fromIndex, int toIndex,double val) { throw new RuntimeException("skeleton method"); }
    public static void fill(float[] a, float val) { throw new RuntimeException("skeleton method"); }
    public static void fill(float[] a, int fromIndex, int toIndex, float val) { throw new RuntimeException("skeleton method"); }
    public static void fill(Object[] a, Object val) { throw new RuntimeException("skeleton method"); }
    public static void fill(Object[] a, int fromIndex, int toIndex, Object val) { throw new RuntimeException("skeleton method"); }
    public static <T> T[] copyOf(T @Readonly [] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static <T,U> T[] copyOf(U @Readonly [] original, int newLength,  Class<? extends T[]> newType) { throw new RuntimeException("skeleton method"); }
    public static byte[] copyOf(byte @Readonly [] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static short[] copyOf(short @Readonly [] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static int[] copyOf(int @Readonly [] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static long[] copyOf(long @Readonly [] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static char[] copyOf(char @Readonly [] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static float[] copyOf(float @Readonly [] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static double[] copyOf(double @Readonly [] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static boolean[] copyOf(boolean @Readonly [] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static <T> T[] copyOfRange(T @Readonly [] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static <T,U> T[] copyOfRange(U @Readonly [] original, int from, int to, Class<? extends T[]> newType) { throw new RuntimeException("skeleton method"); }
    public static byte[] copyOfRange(byte @Readonly [] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static short[] copyOfRange(short @Readonly [] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static int[] copyOfRange(int @Readonly [] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static long[] copyOfRange(long @Readonly [] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static char[] copyOfRange(char @Readonly [] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static float[] copyOfRange(float @Readonly [] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static double[] copyOfRange(double @Readonly [] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static boolean[] copyOfRange(boolean @Readonly [] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    // In JDK7, should instead be: @SafeVarargs
    @SuppressWarnings({"varargs","unchecked"})
    public static <T> List<T> asList(T... a) { throw new RuntimeException("skeleton method"); }
    public static int hashCode(@Readonly long a[]) { throw new RuntimeException("skeleton method"); }
    public static int hashCode(@Readonly int a[]) { throw new RuntimeException("skeleton method"); }
    public static int hashCode(@Readonly short a[]) { throw new RuntimeException("skeleton method"); }
    public static int hashCode(@Readonly char a[]) { throw new RuntimeException("skeleton method"); }
    public static int hashCode(@Readonly byte a[]) { throw new RuntimeException("skeleton method"); }
    public static int hashCode(@Readonly boolean a[]) { throw new RuntimeException("skeleton method"); }
    public static int hashCode(@Readonly float a[]) { throw new RuntimeException("skeleton method"); }
    public static int hashCode(@Readonly double a[]) { throw new RuntimeException("skeleton method"); }
    public static int hashCode(@Readonly Object @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static int deepHashCode(@Readonly Object @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static boolean deepEquals(@Readonly Object @Readonly [] a1, @Readonly Object @Readonly [] a2) { throw new RuntimeException("skeleton method"); }
    public static String toString(long @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(int @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(short @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(char @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(byte @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(boolean @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(float @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(double @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(@Readonly Object @Readonly [] a) { throw new RuntimeException("skeleton method"); }
    public static String deepToString(@Readonly Object @Readonly [] a) { throw new RuntimeException("skeleton method"); }
}
