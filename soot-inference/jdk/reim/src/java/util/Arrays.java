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
    public static int binarySearch(@Readonly long[] a, long key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly long[] a, int fromIndex, int toIndex, long key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly int[] a, int key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly int[] a, int fromIndex, int toIndex, int key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly short[] a, short key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly short[] a, int fromIndex, int toIndex, short key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly char[] a, char key ) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly char[] a, int fromIndex, int toIndex, char key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly byte[] a, byte key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly byte[] a, int fromIndex, int toIndex, byte key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly double[] a, double key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly double[] a, int fromIndex, int toIndex, double key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly float[] a, float key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly float[] a, int fromIndex, int toIndex, float key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly Object[] a, @Readonly Object key) { throw new RuntimeException("skeleton method"); }
    public static int binarySearch(@Readonly Object[] a, int fromIndex, int toIndex, @Readonly Object key) { throw new RuntimeException("skeleton method"); }
    public static <T> int binarySearch(@Readonly T[] a, @Readonly T key,  Comparator<? super T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> int binarySearch(@Readonly T[] a, int fromIndex, int toIndex, @Readonly T key, Comparator<? super T> c) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(@Readonly long[] a, @Readonly long[] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(@Readonly int[] a, @Readonly int[] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(@Readonly short[] a, @Readonly short[] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(@Readonly char[] a, @Readonly char[] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(@Readonly byte[] a, @Readonly byte[] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(@Readonly boolean[] a, @Readonly boolean[] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(@Readonly double[] a, @Readonly double[] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(@Readonly float[] a, @Readonly float[] a2) { throw new RuntimeException("skeleton method"); }
    public static boolean equals(@Readonly Object[] a, @Readonly Object[] a2) { throw new RuntimeException("skeleton method"); }
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
    public static <T> T[] copyOf(@Readonly T[] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static <T,U> T[] copyOf(@Readonly U[] original, int newLength,  Class<? extends T[]> newType) { throw new RuntimeException("skeleton method"); }
    public static byte[] copyOf(@Readonly byte[] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static short[] copyOf(@Readonly short[] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static int[] copyOf(@Readonly int[] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static long[] copyOf(@Readonly long[] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static char[] copyOf(@Readonly char[] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static float[] copyOf(@Readonly float[] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static double[] copyOf(@Readonly double[] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static boolean[] copyOf(@Readonly boolean[] original, int newLength) { throw new RuntimeException("skeleton method"); }
    public static <T> T[] copyOfRange(@Readonly T[] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static <T,U> T[] copyOfRange(@Readonly U[] original, int from, int to, Class<? extends T[]> newType) { throw new RuntimeException("skeleton method"); }
    public static byte[] copyOfRange(@Readonly byte[] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static short[] copyOfRange(@Readonly short[] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static int[] copyOfRange(@Readonly int[] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static long[] copyOfRange(@Readonly long[] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static char[] copyOfRange(@Readonly char[] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static float[] copyOfRange(@Readonly float[] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static double[] copyOfRange(@Readonly double[] original, int from, int to) { throw new RuntimeException("skeleton method"); }
    public static boolean[] copyOfRange(@Readonly boolean[] original, int from, int to) { throw new RuntimeException("skeleton method"); }
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
    public static int hashCode(@Readonly Object[] a) { throw new RuntimeException("skeleton method"); }
    public static int deepHashCode(@Readonly Object[] a) { throw new RuntimeException("skeleton method"); }
    public static boolean deepEquals(@Readonly Object[] a1, @Readonly Object[] a2) { throw new RuntimeException("skeleton method"); }
    public static String toString(@Readonly long[] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(@Readonly int[] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(@Readonly short[] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(@Readonly char[] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(@Readonly byte[] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(@Readonly boolean[] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(@Readonly float[] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(@Readonly double[] a) { throw new RuntimeException("skeleton method"); }
    public static String toString(@Readonly Object[] a) { throw new RuntimeException("skeleton method"); }
    public static String deepToString(@Readonly Object[] a) { throw new RuntimeException("skeleton method"); }
}
