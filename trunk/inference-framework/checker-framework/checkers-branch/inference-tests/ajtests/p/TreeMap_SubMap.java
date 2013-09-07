/**
 * 
 */
package p;



/*internal*/class TreeMap_SubMap
extends AbstractMap
implements SortedMap, java.io.Serializable {
	
	/*atomic(M)*/ private final TreeMap treeMap/*M=this.M*/;
	
	/*atomic(M)*/ private static final long serialVersionUID = -6520786458950516097L;

	/*atomic(M)*/ private boolean toEnd;

	/*atomic(M)*/ private boolean fromStart;

	/*atomic(M)*/ private Object fromKey;

	/*atomic(M)*/ private Object toKey;

	public TreeMap/*M=this.M*/ getTreeMap(){ // FT: added
		return treeMap;
	}
	
	public boolean getFromStart(){ // FT: added
		return fromStart;
	}
	
	public boolean getToEnd(){ // FT: added
		return toEnd;
	}
	
	public Object getFromKey(){ // FT: added
		return fromKey;
	}
	
	public Object getToKey(){ // FT: added
		return toKey;
	}
	
	TreeMap_SubMap(TreeMap tm/*M=this.M*/, Object fromKey, Object toKey) {
    	treeMap = tm;
        if (treeMap.compare(fromKey, toKey) > 0)
            throw new IllegalArgumentException("fromKey > toKey");
        this.fromKey = fromKey;
        this.toKey = toKey;
    }

    TreeMap_SubMap(TreeMap tm/*M=this.M*/, Object key, boolean headMap) {
    	treeMap = tm;
        treeMap.compare(key, key); // Type-check key

        if (headMap) {
            fromStart = true;
            toKey = key;
        } else {
            toEnd = true;
            fromKey = key;
        }
    }

    TreeMap_SubMap(TreeMap tm/*M=this.M*/, boolean fromStart, Object fromKey, boolean toEnd, Object toKey) {
    	treeMap = tm;
        this.fromStart = fromStart;
        this.fromKey= fromKey;
        this.toEnd = toEnd;
        this.toKey = toKey;
    }

    public boolean isEmpty() {
        return entrySet.isEmpty();
    }

    public boolean containsKey(Object key) {
        return inRange((Object) key) && treeMap.containsKey(key);
    }

    public Object get(Object key) {
        if (!inRange((Object) key))
            return null;
        return treeMap.get(key);
    }

    public Object put(Object key, Object value) {
        if (!inRange(key))
            throw new IllegalArgumentException("key out of range");
        return treeMap.put(key, value);
    }

    public Comparator comparator() {
        return treeMap.getComparator();
    }

    public Object firstKey() {
    TreeMap_Entry e/*F=this.M*/ = fromStart ? treeMap.firstEntry() : treeMap.getCeilEntry(fromKey);
        Object first = TreeMap.key(e);
        if (!toEnd && treeMap.compare(first, toKey) >= 0)
            throw(new NoSuchElementException());
        return first;
    }

    public Object lastKey() {
    TreeMap_Entry e/*F=this.M*/ = toEnd ? treeMap.lastEntry() : treeMap.getPrecedingEntry(toKey);
        Object last = TreeMap.key(e);
        if (!fromStart && treeMap.compare(last, fromKey) < 0)
            throw(new NoSuchElementException());
        return last;
    }

    /*atomic(M)*/ private transient Set entrySet/*L=this.M*/ = new TreeMap_EntrySetView/*L=this.M*/(this);

    public Set/*L=this.M*/ entrySet() {
        return entrySet;
    }

    public SortedMap/*M=this.M*/ subMap(Object fromKey, Object toKey) {
        if (!inRange2(fromKey))
            throw new IllegalArgumentException("fromKey out of range");
        if (!inRange2(toKey))
            throw new IllegalArgumentException("toKey out of range");
        return new TreeMap_SubMap/*M=this.M*/(treeMap, fromKey, toKey);
    }

    public SortedMap/*M=this.M*/ headMap(Object toKey) {
        if (!inRange2(toKey))
            throw new IllegalArgumentException("toKey out of range");
        return new TreeMap_SubMap/*M=this.M*/(treeMap, fromStart, fromKey, false, toKey);
    }

    public SortedMap/*M=this.M*/ tailMap(Object fromKey) {
        if (!inRange2(fromKey))
            throw new IllegalArgumentException("fromKey out of range");
        return new TreeMap_SubMap/*M=this.M*/(treeMap, false, fromKey, toEnd, toKey);
    }

    boolean inRange(Object key) {
        return (fromStart || treeMap.compare(key, fromKey) >= 0) &&
               (toEnd     || treeMap.compare(key, toKey)   <  0);
    }

    // This form allows the high endpoint (as well as all legit keys)
    private boolean inRange2(Object key) {
        return (fromStart || treeMap.compare(key, fromKey) >= 0) &&
               (toEnd     || treeMap.compare(key, toKey)   <= 0);
    }
}
