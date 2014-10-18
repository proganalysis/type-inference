package java.util;
import checkers.inference2.reimN.quals.*;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;

public class Collections {
    private Collections() { throw new RuntimeException("skeleton method"); }
    public static <T extends Comparable<? super T>> void sort(List<T> list) { throw new RuntimeException("skeleton method"); }
    public static <T> void sort(List<T> list,  Comparator<? super T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> int binarySearch(@ReadRead List< ? extends Comparable<? super T>> list, @ReadRead T key) { throw new RuntimeException("skeleton method"); }
    public static <T> int binarySearch(@ReadRead List<? extends T> list, @ReadRead T key, Comparator<? super T> c) { throw new RuntimeException("skeleton method"); }
    public static void reverse(List<?> list) { throw new RuntimeException("skeleton method"); }
    public static void shuffle(List<?> list) { throw new RuntimeException("skeleton method"); }
    public static void shuffle(List<?> list,  Random rnd) { throw new RuntimeException("skeleton method"); }
    public static void swap(List<?> list, int i, int j) { throw new RuntimeException("skeleton method"); }
    public static <T> void fill(List<? super T> list, T obj) { throw new RuntimeException("skeleton method"); }
    public static <T> void copy(List<? super T> dest,  @ReadRead List<? extends T> src) { throw new RuntimeException("skeleton method"); }
    public static <T extends @ReadRead Object & Comparable<? super T>> T min(@ReadRead Collection<? extends T> coll) { throw new RuntimeException("skeleton method"); }
    public static <T> T min(@ReadRead Collection<? extends T> coll, Comparator<? super T> comp) { throw new RuntimeException("skeleton method"); }
    public static <T extends @ReadRead Object & Comparable<? super T>> T max(@ReadRead Collection<? extends T> coll) { throw new RuntimeException("skeleton method"); }
    public static <T> T max(@ReadRead Collection<? extends T> coll,  Comparator<? super T> comp) { throw new RuntimeException("skeleton method"); }
    public static void rotate(List<?> list, int distance) { throw new RuntimeException("skeleton method"); }
    public static <T> boolean replaceAll(List<T> list, T oldVal, T newVal) { throw new RuntimeException("skeleton method"); }
    public static int indexOfSubList(@ReadRead List<?> source, @ReadRead List<?> target) { throw new RuntimeException("skeleton method"); }
    public static int lastIndexOfSubList(@ReadRead List<?> source, @ReadRead List<?> target) { throw new RuntimeException("skeleton method"); }
    public static <T>  @ReadRead Collection<T> unmodifiableCollection(@ReadRead Collection<? extends T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> @ReadRead Set<T> unmodifiableSet(@ReadRead Set<? extends T> s) { throw new RuntimeException("skeleton method"); }
    public static <T>  @ReadRead SortedSet<T> unmodifiableSortedSet(@ReadRead SortedSet<T> s) { throw new RuntimeException("skeleton method"); }
    public static <T> @ReadRead List<T> unmodifiableList(@ReadRead List<? extends T> list) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @ReadRead Map<K,V> unmodifiableMap(@ReadRead Map<? extends K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @ReadRead SortedMap<K,V> unmodifiableSortedMap(@ReadRead SortedMap<K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public static <T> @PolyPoly Collection<T> synchronizedCollection(@PolyPoly Collection<T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> @PolyPoly Set<T> synchronizedSet(@PolyPoly Set<T> s) { throw new RuntimeException("skeleton method"); }
    public static <T> @PolyPoly SortedSet<T> synchronizedSortedSet(@PolyPoly SortedSet<T> s) { throw new RuntimeException("skeleton method"); }
    public static <T> @PolyPoly List<T> synchronizedList(@PolyPoly List<T> list) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @PolyPoly Map<K,V> synchronizedMap(@PolyPoly Map<K,V> m) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @PolyPoly SortedMap<K,V> synchronizedSortedMap(@PolyPoly SortedMap<K,V> m) { throw new RuntimeException("skeleton method"); }
    public static <E> @PolyPoly Collection<E> checkedCollection(@PolyPoly Collection<E> c, Class<E> type) { throw new RuntimeException("skeleton method"); }
    public static <E> @PolyPoly Set<E> checkedSet(@PolyPoly Set<E> s, Class<E> type) { throw new RuntimeException("skeleton method"); }
    public static <E> @PolyPoly SortedSet<E> checkedSortedSet(@PolyPoly SortedSet<E> s, Class<E> type) { throw new RuntimeException("skeleton method"); }
    public static <E> @PolyPoly List<E> checkedList(@PolyPoly List<E> list, Class<E> type) { throw new RuntimeException("skeleton method"); }
    public static <K, V> @PolyPoly Map<K, V> checkedMap(@PolyPoly Map<K, V> m, Class<K> keyType, Class<V> valueType) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @PolyPoly SortedMap<K,V> checkedSortedMap(@PolyPoly SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) { throw new RuntimeException("skeleton method"); }
    @SuppressWarnings("rawtypes") public static final @ReadRead Set EMPTY_SET = null;
    public static final <T> @ReadRead Set<T> emptySet() { throw new RuntimeException("skeleton method"); }
    @SuppressWarnings("rawtypes") public static final @ReadRead List EMPTY_LIST = null;
    public static final <T> @ReadRead List<T> emptyList() { throw new RuntimeException("skeleton method"); }
    @SuppressWarnings("rawtypes") public static final @ReadRead Map EMPTY_MAP = null;
    public static final <K,V>  @ReadRead Map<K,V> emptyMap() { throw new RuntimeException("skeleton method"); }
    public static <T> @ReadRead Set<T> singleton(T o) { throw new RuntimeException("skeleton method"); }
    public static <T> @ReadRead List<T> singletonList(T o) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @ReadRead Map<K,V> singletonMap(K key, V value) { throw new RuntimeException("skeleton method"); }
    public static <T> @ReadRead List<T> nCopies(int n, T o) { throw new RuntimeException("skeleton method"); }
    public static <T> Comparator<T> reverseOrder() { throw new RuntimeException("skeleton method"); }
    public static <T> Comparator<T> reverseOrder(Comparator<T> cmp) { throw new RuntimeException("skeleton method"); }
    public static <T> @PolyPoly Enumeration<T> enumeration(final @ReadRead Collection<T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> @PolyPoly ArrayList<T> list(@PolyPoly Enumeration<T> e) { throw new RuntimeException("skeleton method"); }
    public static int frequency(@ReadRead Collection<?> c, @ReadRead Object o) { throw new RuntimeException("skeleton method"); }
    public static boolean disjoint(@ReadRead Collection<?> c1, @ReadRead Collection<?> c2) { throw new RuntimeException("skeleton method"); }
    // In JDK7, should instead be: @SafeVarargs
    @SuppressWarnings({"varargs","unchecked"})
    public static <T> boolean addAll(Collection<? super T> c, T... elements) { throw new RuntimeException("skeleton method"); }
    public static <E> @PolyPoly Set<E> newSetFromMap(@PolyPoly Map<E, Boolean> map) { throw new RuntimeException("skeleton method"); }
    public static <T> @PolyPoly Queue<T> asLifoQueue(@PolyPoly Deque<T> deque) { throw new RuntimeException("skeleton method"); }
}
