package java.util;
import checkers.inference.reim.quals.*;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;

public class Collections {
    private Collections() { throw new RuntimeException("skeleton method"); }
    public static <T extends Comparable<? super T>> void sort(List<T> list) { throw new RuntimeException("skeleton method"); }
    public static <T> void sort(List<T> list,  Comparator<? super T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> int binarySearch(@Readonly List< ? extends Comparable<? super T>> list, @Readonly T key) { throw new RuntimeException("skeleton method"); }
    public static <T> int binarySearch(@Readonly List<? extends T> list, @Readonly T key, Comparator<? super T> c) { throw new RuntimeException("skeleton method"); }
    public static void reverse(List<?> list) { throw new RuntimeException("skeleton method"); }
    public static void shuffle(List<?> list) { throw new RuntimeException("skeleton method"); }
    public static void shuffle(List<?> list,  Random rnd) { throw new RuntimeException("skeleton method"); }
    public static void swap(List<?> list, int i, int j) { throw new RuntimeException("skeleton method"); }
    public static <T> void fill(List<? super T> list, T obj) { throw new RuntimeException("skeleton method"); }
    public static <T> void copy(List<? super T> dest,  @Readonly List<? extends T> src) { throw new RuntimeException("skeleton method"); }
    public static <T extends @Readonly Object & Comparable<? super T>> T min(@Readonly Collection<? extends T> coll) { throw new RuntimeException("skeleton method"); }
    public static <T> T min(@Readonly Collection<? extends T> coll, Comparator<? super T> comp) { throw new RuntimeException("skeleton method"); }
    public static <T extends @Readonly Object & Comparable<? super T>> T max(@Readonly Collection<? extends T> coll) { throw new RuntimeException("skeleton method"); }
    public static <T> T max(@Readonly Collection<? extends T> coll,  Comparator<? super T> comp) { throw new RuntimeException("skeleton method"); }
    public static void rotate(List<?> list, int distance) { throw new RuntimeException("skeleton method"); }
    public static <T> boolean replaceAll(List<T> list, T oldVal, T newVal) { throw new RuntimeException("skeleton method"); }
    public static int indexOfSubList(@Readonly List<?> source, @Readonly List<?> target) { throw new RuntimeException("skeleton method"); }
    public static int lastIndexOfSubList(@Readonly List<?> source, @Readonly List<?> target) { throw new RuntimeException("skeleton method"); }
    public static <T>  @Readonly Collection<T> unmodifiableCollection(@Readonly Collection<? extends T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> @Readonly Set<T> unmodifiableSet(@Readonly Set<? extends T> s) { throw new RuntimeException("skeleton method"); }
    public static <T>  @Readonly SortedSet<T> unmodifiableSortedSet(@Readonly SortedSet<T> s) { throw new RuntimeException("skeleton method"); }
    public static <T> @Readonly List<T> unmodifiableList(@Readonly List<? extends T> list) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @Readonly Map<K,V> unmodifiableMap(@Readonly Map<? extends K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @Readonly SortedMap<K,V> unmodifiableSortedMap(@Readonly SortedMap<K, ? extends V> m) { throw new RuntimeException("skeleton method"); }
    public static <T> @Polyread Collection<T> synchronizedCollection(@Polyread Collection<T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> @Polyread Set<T> synchronizedSet(@Polyread Set<T> s) { throw new RuntimeException("skeleton method"); }
    public static <T> @Polyread SortedSet<T> synchronizedSortedSet(@Polyread SortedSet<T> s) { throw new RuntimeException("skeleton method"); }
    public static <T> @Polyread List<T> synchronizedList(@Polyread List<T> list) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @Polyread Map<K,V> synchronizedMap(@Polyread Map<K,V> m) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @Polyread SortedMap<K,V> synchronizedSortedMap(@Polyread SortedMap<K,V> m) { throw new RuntimeException("skeleton method"); }
    public static <E> @Polyread Collection<E> checkedCollection(@Polyread Collection<E> c, Class<E> type) { throw new RuntimeException("skeleton method"); }
    public static <E> @Polyread Set<E> checkedSet(@Polyread Set<E> s, Class<E> type) { throw new RuntimeException("skeleton method"); }
    public static <E> @Polyread SortedSet<E> checkedSortedSet(@Polyread SortedSet<E> s, Class<E> type) { throw new RuntimeException("skeleton method"); }
    public static <E> @Polyread List<E> checkedList(@Polyread List<E> list, Class<E> type) { throw new RuntimeException("skeleton method"); }
    public static <K, V> @Polyread Map<K, V> checkedMap(@Polyread Map<K, V> m, Class<K> keyType, Class<V> valueType) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @Polyread SortedMap<K,V> checkedSortedMap(@Polyread SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) { throw new RuntimeException("skeleton method"); }
    @SuppressWarnings("rawtypes") public static final @Readonly Set EMPTY_SET = null;
    public static final <T> @Readonly Set<T> emptySet() { throw new RuntimeException("skeleton method"); }
    @SuppressWarnings("rawtypes") public static final @Readonly List EMPTY_LIST = null;
    public static final <T> @Readonly List<T> emptyList() { throw new RuntimeException("skeleton method"); }
    @SuppressWarnings("rawtypes") public static final @Readonly Map EMPTY_MAP = null;
    public static final <K,V>  @Readonly Map<K,V> emptyMap() { throw new RuntimeException("skeleton method"); }
    public static <T> @Readonly Set<T> singleton(T o) { throw new RuntimeException("skeleton method"); }
    public static <T> @Readonly List<T> singletonList(T o) { throw new RuntimeException("skeleton method"); }
    public static <K,V> @Readonly Map<K,V> singletonMap(K key, V value) { throw new RuntimeException("skeleton method"); }
    public static <T> @Readonly List<T> nCopies(int n, T o) { throw new RuntimeException("skeleton method"); }
    public static <T> Comparator<T> reverseOrder() { throw new RuntimeException("skeleton method"); }
    public static <T> Comparator<T> reverseOrder(Comparator<T> cmp) { throw new RuntimeException("skeleton method"); }
    public static <T> @Polyread Enumeration<T> enumeration(final @Readonly Collection<T> c) { throw new RuntimeException("skeleton method"); }
    public static <T> @Polyread ArrayList<T> list(@Polyread Enumeration<T> e) { throw new RuntimeException("skeleton method"); }
    public static int frequency(@Readonly Collection<?> c, @Readonly Object o) { throw new RuntimeException("skeleton method"); }
    public static boolean disjoint(@Readonly Collection<?> c1, @Readonly Collection<?> c2) { throw new RuntimeException("skeleton method"); }
    // In JDK7, should instead be: @SafeVarargs
    @SuppressWarnings({"varargs","unchecked"})
    public static <T> boolean addAll(Collection<? super T> c, T... elements) { throw new RuntimeException("skeleton method"); }
    public static <E> @Polyread Set<E> newSetFromMap(@Polyread Map<E, Boolean> map) { throw new RuntimeException("skeleton method"); }
    public static <T> @Polyread Queue<T> asLifoQueue(@Polyread Deque<T> deque) { throw new RuntimeException("skeleton method"); }
}
