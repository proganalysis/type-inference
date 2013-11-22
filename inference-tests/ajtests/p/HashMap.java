package p;

import checkers.inference.aj.quals.*;

/*
 * @(#)HashMap.java	1.65 05/03/03
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

 
import  java.io.*;

/**
 * Hash table based implementation of the <tt>Map</tt> interface.  This
 * implementation provides all of the optional map operations, and permits
 * <tt>null</tt> values and the <tt>null</tt> key.  (The <tt>HashMap</tt>
 * class is roughly equivalent to <tt>Hashtable</tt>, except that it is
 * unsynchronized and permits nulls.)  This class makes no guarantees as to
 * the order of the map; in particular, it does not guarantee that the order
 * will remain constant over time.
 *
 * <p>This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function
 * disperses the elements properly among the buckets.  Iteration over
 * collection views requires time proportional to the "capacity" of the
 * <tt>HashMap</tt> instance (the number of buckets) plus its size (the number
 * of key-value mappings).  Thus, it's very important not to set the initial
 * capacity too high (or the load factor too low) if iteration performance is
 * important.
 *
 * <p>An instance of <tt>HashMap</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created.  The
 * <i>load factor</i> is a measure of how full the hash table is allowed to
 * get before its capacity is automatically increased.  When the number of
 * entries in the hash table exceeds the product of the load factor and the
 * current capacity, the capacity is roughly doubled by calling the  
 * <tt>rehash</tt> method.
 *
 * <p>As a general rule, the default load factor (.75) offers a good tradeoff
 * between time and space costs.  Higher values decrease the space overhead
 * but increase the lookup cost (reflected in most of the operations of the
 * <tt>HashMap</tt> class, including <tt>get</tt> and <tt>put</tt>).  The
 * expected number of entries in the map and its load factor should be taken
 * into account when setting its initial capacity, so as to minimize the
 * number of <tt>rehash</tt> operations.  If the initial capacity is greater
 * than the maximum number of entries divided by the load factor, no
 * <tt>rehash</tt> operations will ever occur.
 * 
 * <p>If many mappings are to be stored in a <tt>HashMap</tt> instance,
 * creating it with a sufficiently large capacity will allow the mappings to
 * be stored more efficiently than letting it perform automatic rehashing as
 * needed to grow the table.
 *
 * <p><b>Note that this implementation is not synchronized.</b> If multiple
 * threads access this map concurrently, and at least one of the threads
 * modifies the map structurally, it <i>must</i> be synchronized externally.
 * (A structural modification is any operation that adds or deletes one or
 * more mappings; merely changing the value associated with a key that an
 * instance already contains is not a structural modification.)  This is
 * typically accomplished by synchronizing on some object that naturally
 * encapsulates the map.  If no such object exists, the map should be
 * "wrapped" using the <tt>Collections.synchronizedMap</tt> method.  This is
 * best done at creation time, to prevent accidental unsynchronized access to
 * the map: <pre> Map m = Collections.synchronizedMap(new HashMap(...));
 * </pre>
 *
 * <p>The iterators returned by all of this class's "collection view methods"
 * are <i>fail-fast</i>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> or <tt>add</tt> methods, the iterator will throw a
 * <tt>ConcurrentModificationException</tt>.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis. 
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class is a member of the 
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Doug Lea
 * @author  Josh Bloch
 * @author  Arthur van Hoff
 * @author  Neal Gafter
 * @version 1.65, 03/03/05
 * @see     Object#hashCode()
 * @see     Collection
 * @see	    Map
 * @see	    TreeMap
 * @see	    Hashtable
 * @since   1.2
 */

/*internal*/public class HashMap
    extends AbstractMap
    implements Map, Cloneable, Serializable
{

    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     **/
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    /*atomic(M)*/ private transient HashMap_Entry[] table/*this.M[]F=this.M*/;

    /**
     * The number of key-value mappings contained in this identity hash map.
     */
    /*atomic(M)*/ private transient int size;
  
    /**
     * The next size value at which to resize (capacity * load factor).
     * @serial
     */
    /*atomic(M)*/ int threshold;
  
    /**
     * The load factor for the hash table.
     *
     * @serial
     */
    /*atomic(M)*/ private final float loadFactor;

    /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the HashMap fail-fast.  (See ConcurrentModificationException).
     */
    /*atomic(M)*/ private transient volatile int modCount; // FT: made private

    public int getModCount(){ // FT: added
    	return modCount;
    }
    
    public void setModCount(int m){ // FT: added
    	modCount = m;
    }
    
    public int getSize(){ // FT: added
    	return size;
    }
    
    public void setSize(int s){ // FT: added
    	size = s;
    }
    
    public HashMap_Entry[]/*this.M[]F=this.M*/ getTable(){ // FT: added
    	return table;
    }
    
    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     *
     * @param  initialCapacity The initial capacity.
     * @param  loadFactor      The load factor.
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive.
     */
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity) 
            capacity <<= 1;
    
        this.loadFactor = loadFactor;
        threshold = (int)(capacity * loadFactor);
        table = new HashMap_Entry/*this.M[]F=this.M*/[capacity];
        init();
    }
  
    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param  initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new HashMap_Entry/*this.M[]F=this.M*/[DEFAULT_INITIAL_CAPACITY];
        init();
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the
     * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
     * default load factor (0.75) and an initial capacity sufficient to
     * hold the mappings in the specified <tt>Map</tt>.
     *
     * @param   m the map whose mappings are to be placed in this map.
     * @throws  NullPointerException if the specified map is null.
     */
    public HashMap(/*unitfor(M)*/ /*@Aliased*/ Map/*M=this.M*/ m) {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
                      DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        putAllForCreate(m);
    }

    // internal utilities

    /**
     * Initialization hook for subclasses. This method is called
     * in all constructors and pseudo-constructors (clone, readObject)
     * after HashMap has been initialized but before any entries have
     * been inserted.  (In the absence of this method, readObject would
     * require explicit knowledge of subclasses.)
     */
    void init() {
    }

    /**
     * Value representing null keys inside tables.
     */
    static final Object NULL_KEY = new Object();

    /**
     * Returns internal representation for key. Use NULL_KEY if key is null.
     */
    static Object maskNull(Object key) {
        return key == null ? (Object)NULL_KEY : key;
    }

    /**
     * Returns key represented by specified internal representation.
     */
    static Object unmaskNull(Object key) {
        return (key == NULL_KEY ? null : key);
    }

    /**
     * Returns a hash value for the specified object.  In addition to 
     * the object's own hashCode, this method applies a "supplemental
     * hash function," which defends against poor quality hash functions.
     * This is critical because HashMap uses power-of two length 
     * hash tables.<p>
     *
     * The shift distances in this function were chosen as the result
     * of an automated search over the entire four-dimensional search space.
     */
    static int hash(Object x) {
        int h = x.hashCode();

        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
    }

    /** 
     * Check for equality of non-null reference x and possibly-null y. 
     */
    static boolean eq(Object x, Object y) {
        return x == y || x.equals(y);
    }

    /**
     * Returns index for hash code h. 
     */
    static int indexFor(int h, int length) {
        return h & (length-1);
    }
 
    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map.
     */
    public int size() {
        return size;
    }
  
    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped in this identity
     * hash map, or <tt>null</tt> if the map contains no mapping for this key.
     * A return value of <tt>null</tt> does not <i>necessarily</i> indicate
     * that the map contains no mapping for the key; it is also possible that
     * the map explicitly maps the key to <tt>null</tt>. The
     * <tt>containsKey</tt> method may be used to distinguish these two cases.
     *
     * @param   key the key whose associated value is to be returned.
     * @return  the value to which this map maps the specified key, or
     *          <tt>null</tt> if the map contains no mapping for this key.
     * @see #put(Object, Object)
     */
    public Object get(Object key) {
        Object k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash, table.length);
        HashMap_Entry e/*F=this.M*/ = table[i]; 
        while (true) {
            if (e == null)
                return null;
            if (e.getHash() == hash && eq(k, e.getKey())) 
                return e.getValue();
            e = e.getNext();
        }
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(Object key) {
        Object k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash, table.length);
        HashMap_Entry e/*F=this.M*/ = table[i]; 
        while (e != null) {
            if (e.getHash() == hash && eq(k, e.getKey())) 
                return true;
            e = e.getNext();
        }
        return false;
    }

    /**
     * Returns the entry associated with the specified key in the
     * HashMap.  Returns null if the HashMap contains no mapping
     * for this key.
     */
    HashMap_Entry/*F=this.M*/ getEntry(Object key) {
        Object k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash, table.length);
        HashMap_Entry e/*F=this.M*/ = table[i]; 
        while (e != null && !(e.getHash() == hash && eq(k, e.getKey())))
            e = e.getNext();
        return e;
    }
  
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for this key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the HashMap previously associated
     *	       <tt>null</tt> with the specified key.
     */
    public Object put(Object key, Object value) {
	Object k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash, table.length);

        for (HashMap_Entry e/*F=this.M*/ = table[i]; e != null; e = e.getNext()) {
            if (e.getHash() == hash && eq(k, e.getKey())) {
                Object oldValue = e.getValue();
                e.setValue(value);
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, k, value, i);
        return null;
    }

    /**
     * This method is used instead of put by constructors and
     * pseudoconstructors (clone, readObject).  It does not resize the table,
     * check for comodification, etc.  It calls createEntry rather than
     * addEntry.
     */
    private void putForCreate(Object key, Object value) {
        Object k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash, table.length);

        /**
         * Look for preexisting entry for key.  This will never happen for
         * clone or deserialize.  It will only happen for construction if the
         * input Map is a sorted map whose ordering is inconsistent w/ equals.
         */
        for (HashMap_Entry e/*F=this.M*/ = table[i]; e != null; e = e.getNext()) {
            if (e.getHash() == hash && eq(k, e.getKey())) {
                e.setValue(value);
                return;
            }
        }

        createEntry(hash, k, value, i);
    }

    void putAllForCreate(/*unitfor(M)*/ /*@Aliased*/ Map/*M=this.M*/ m) {
        for (Iterator i/*I=this.M*/ = m.entrySet().iterator(); i.hasNext(); ) {
            Map_Entry e/*F=this.M*/ = (Map_Entry/*F=this.M*/)i.next();
            putForCreate(e.getKey(), e.getValue());
        }
    }

    /**
     * Rehashes the contents of this map into a new array with a
     * larger capacity.  This method is called automatically when the
     * number of keys in this map reaches its threshold.
     *
     * If current capacity is MAXIMUM_CAPACITY, this method does not
     * resize the map, but sets threshold to Integer.MAX_VALUE.
     * This has the effect of preventing future calls.
     *
     * @param newCapacity the new capacity, MUST be a power of two;
     *        must be greater than current capacity unless current
     *        capacity is MAXIMUM_CAPACITY (in which case value
     *        is irrelevant).
     */
    void resize(int newCapacity) {
        HashMap_Entry[] oldTable/*this.M[]F=this.M*/ = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        HashMap_Entry[] newTable/*this.M[]F=this.M*/ = new HashMap_Entry/*this.M[]F=this.M*/[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }

    /** 
     * Transfer all entries from current table to newTable.
     */
    private void transfer(HashMap_Entry[] newTable/*this.M[]F=this.M*/) {
        HashMap_Entry[] src/*this.M[]F=this.M*/ = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            HashMap_Entry e/*F=this.M*/ = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    HashMap_Entry next/*F=this.M*/ = e.getNext();
                    int i = indexFor(e.getHash(), newCapacity);  
                    e.setNext(newTable[i]);
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map
     * These mappings will replace any mappings that
     * this map had for any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map.
     * @throws NullPointerException if the specified map is null.
     */
    public void putAll(/*unitfor(M)*/ /*@Aliased*/ Map/*M=this.M*/ m) {
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0)
            return;

        /*
         * Expand the map if the map if the number of mappings to be added
         * is greater than or equal to threshold.  This is conservative; the
         * obvious condition is (m.size() + size) >= threshold, but this
         * condition could result in a map with twice the appropriate capacity,
         * if the keys to be added overlap with the keys already in this map.
         * By using the conservative calculation, we subject ourself
         * to at most one extra resize.
         */
        if (numKeysToBeAdded > threshold) {
            int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY)
                targetCapacity = MAXIMUM_CAPACITY;
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            if (newCapacity > table.length)
                resize(newCapacity);
        }

        for (Iterator i/*I=this.M*/ = m.entrySet().iterator(); i.hasNext(); ) {
            Map_Entry e/*F=this.M*/ = (Map_Entry/*F=this.M*/)i.next();
            put(e.getKey(), e.getValue());
        }
    }
  
    /**
     * Removes the mapping for this key from this map if present.
     *
     * @param  key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the map previously associated <tt>null</tt>
     *	       with the specified key.
     */
    public Object remove(Object key) {
        HashMap_Entry e/*F=this.M*/ = removeEntryForKey(key);
        return (e == null ? null : e.getValue());
    }

    /**
     * Removes and returns the entry associated with the specified key
     * in the HashMap.  Returns null if the HashMap contains no mapping
     * for this key.
     */
    HashMap_Entry/*F=this.M*/ removeEntryForKey(Object key) {
        Object k = maskNull(key);
        int hash = hash(k);
        int i = indexFor(hash, table.length);
        HashMap_Entry prev/*F=this.M*/ = table[i];
        HashMap_Entry e/*F=this.M*/ = prev;

        while (e != null) {
            HashMap_Entry next/*F=this.M*/ = e.getNext();
            if (e.getHash() == hash && eq(k, e.getKey())) {
                modCount++;
                size--;
                if (prev == e) 
                    table[i] = next;
                else
                    prev.setNext(next);
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }
   
        return e;
    }

    /**
     * Special version of remove for EntrySet.
     */
    HashMap_Entry/*F=this.M*/ removeMapping(Object o) {
        if (!(o instanceof Map_Entry))
            return null;

        Map_Entry entry/*F=this.M*/ = (Map_Entry/*F=this.M*/) o;
        Object k = maskNull(entry.getKey());
        int hash = hash(k);
        int i = indexFor(hash, table.length);
        HashMap_Entry prev/*F=this.M*/ = table[i];
        HashMap_Entry e/*F=this.M*/ = prev;

        while (e != null) {
            HashMap_Entry next/*F=this.M*/ = e.getNext();
            if (e.getHash() == hash && e.equals(entry)) {
                modCount++;
                size--;
                if (prev == e) 
                    table[i] = next;
                else
                    prev.setNext(next);
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }
   
        return e;
    }

    /**
     * Removes all mappings from this map.
     */
    public void clear() {
        modCount++;
        HashMap_Entry[] tab/*this.M[]F=this.M*/  = table;
        for (int i = 0; i < tab.length; i++) 
            tab[i] = null;
        size = 0;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value.
     */
    public boolean containsValue(Object value) {
	if (value == null) 
            return containsNullValue();

	HashMap_Entry[] tab/*this.M[]F=this.M*/ = table;
        for (int i = 0; i < tab.length ; i++)
            for (HashMap_Entry e/*F=this.M*/ = tab[i] ; e != null ; e = e.getNext())
                if (value.equals(e.getValue()))
                    return true;
	return false;
    }

    /**
     * Special-case code for containsValue with null argument
     **/
    private boolean containsNullValue() {
	HashMap_Entry[] tab/*this.M[]F=this.M*/ = table;
        for (int i = 0; i < tab.length ; i++)
            for (HashMap_Entry e/*F=this.M*/ = tab[i] ; e != null ; e = e.getNext())
                if (e.getValue() == null)
                    return true;
	return false;
    }

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
     * values themselves are not cloned.
     *
     * @return a shallow copy of this map.
     */
// clone() generated by our compiler
//    public Object clone() {
//        HashMap result = null;
//	try { 
//	    result = (HashMap)super.clone();
//	} catch (CloneNotSupportedException e) { 
//	    // assert false;
//	}
//        result.table = new HashMap_Entry[table.length];
//        result.entrySet = null;
//        result.modCount = 0;
//        result.size = 0;
//        result.init();
//        result.putAllForCreate(this);
//
//        return result;
//    }

    /**
     * Add a new entry with the specified key, value and hash code to
     * the specified bucket.  It is the responsibility of this 
     * method to resize the table if appropriate.
     *
     * Subclass overrides this to alter the behavior of put method.
     */
    void addEntry(int hash, Object key, Object value, int bucketIndex) {
	HashMap_Entry e/*F=this.M*/ = table[bucketIndex];
        table[bucketIndex] = new HashMap_Entry/*F=this.M*/(hash, key, value, e);
        if (size++ >= threshold)
            resize(2 * table.length);
    }

    /**
     * Like addEntry except that this version is used when creating entries
     * as part of Map construction or "pseudo-construction" (cloning,
     * deserialization).  This version needn't worry about resizing the table.
     *
     * Subclass overrides this to alter the behavior of HashMap(Map),
     * clone, and readObject.
     */
    void createEntry(int hash, Object key, Object value, int bucketIndex) {
	HashMap_Entry e/*F=this.M*/ = table[bucketIndex];
        table[bucketIndex] = new HashMap_Entry/*F=this.M*/(hash, key, value, e);
        size++;
    }

    // Subclass overrides these to alter behavior of views' iterator() method
    /*Aliased*/ Iterator/*I=this.M*/ newKeyIterator()   {
        return (Iterator/*I=this.M*/) new HashMap_KeyIterator/*I=this.M*/(this);
    }
    /*Aliased*/ Iterator/*I=this.M*/ newValueIterator()   {
        return (Iterator/*I=this.M*/) new HashMap_ValueIterator/*I=this.M*/(this);
    }
    /*Aliased*/ Iterator/*I=this.M*/ newEntryIterator()   {
        return (Iterator/*I=this.M*/) new HashMap_EntryIterator/*I=this.M*/(this);
    }


    // Views

    /*atomic(M)*/ private transient Set entrySet/*L=this.M*/  = null;

    /**
     * Returns a set view of the keys contained in this map.  The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.  The set supports element removal, which removes the
     * corresponding mapping from this map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
     * <tt>clear</tt> operations.  It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this map.
     */
    public /*Aliased*/ Set/*L=this.M*/ keySet() {
        Set ks/*L=this.M*/ = getKeySet();
        return (ks != null ? ks : (setKeySet(new HashMap_KeySet/*L=this.M*/(this))));
    }

    /**
     * Returns a collection view of the values contained in this map.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from this map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this map.
     */
    public /*Aliased*/ Collection/*L=this.M*/ values() {
        Collection vs/*L=this.M*/ = getValues();
        return (vs != null ? vs : (setValues(new HashMap_Values/*L=this.M*/(this))));
    }

    /**
     * Returns a collection view of the mappings contained in this map.  Each
     * element in the returned collection is a <tt>Map.Entry</tt>.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the mappings contained in this map.
     * @see Map_Entry
     */
    public /*Aliased*/ Set/*L=this.M*/ entrySet() {
        Set es/*L=this.M*/ = entrySet;
        return (es != null ? es : (entrySet = new HashMap_EntrySet/*L=this.M*/(this)));
    }

    /**
     * Save the state of the <tt>HashMap</tt> instance to a stream (i.e.,
     * serialize it).
     *
     * @serialData The <i>capacity</i> of the HashMap (the length of the
     *		   bucket array) is emitted (int), followed  by the
     *		   <i>size</i> of the HashMap (the number of key-value
     *		   mappings), followed by the key (Object) and value (Object)
     *		   for each key-value mapping represented by the HashMap
     *             The key-value mappings are emitted in the order that they
     *             are returned by <tt>entrySet().iterator()</tt>.
     * 
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
	Iterator i/*I=this.M*/ = entrySet().iterator();

	// Write out the threshold, loadfactor, and any hidden stuff
	s.defaultWriteObject();

	// Write out number of buckets
	s.writeInt(table.length);

	// Write out size (number of Mappings)
	s.writeInt(size);

        // Write out keys and values (alternating)
	while (i.hasNext()) { 
            Map_Entry e/*F=this.M*/ = (Map_Entry/*F=this.M*/)i.next();
            s.writeObject(e.getKey());
            s.writeObject(e.getValue());
        }
    }

    private static final long serialVersionUID = 362498820763181265L;

    /**
     * Reconstitute the <tt>HashMap</tt> instance from a stream (i.e.,
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
	// Read in the threshold, loadfactor, and any hidden stuff
	s.defaultReadObject();

	// Read in number of buckets and allocate the bucket array;
	int numBuckets = s.readInt();
	table = new HashMap_Entry/*this.M[]F=this.M*/[numBuckets];

        init();  // Give subclass a chance to do its thing.

	// Read in size (number of Mappings)
	int size = s.readInt(); 

	// Read the keys and values, and put the mappings in the HashMap
	for (int i=0; i<size; i++) {
	    Object key = (Object) s.readObject();
	    Object value = (Object) s.readObject();
	    putForCreate(key, value);
	}
    }

    // These methods are used when serializing HashSets
    int   capacity()     { return table.length; }
    float loadFactor()   { return loadFactor;   }
}

