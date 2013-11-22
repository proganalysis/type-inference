package p;

/**
 * TreeMap Iterator.
 */
/*internal*/abstract class TreeMap_PrivateEntryIterator implements Iterator {
	
	/*atomic(I)*/ private final TreeMap treeMap/*M=this.I*/;
	/*atomic(I)*/ private int expectedModCount;
	/*atomic(I)*/ private TreeMap_Entry lastReturned/*F=this.I*/ = null;
	/*atomic(I)*/ private TreeMap_Entry next/*F=this.I*/;

	public TreeMap_Entry/*F=this.I*/ getNext(){ // FT: added
		return next;
	}
	
    TreeMap_PrivateEntryIterator(TreeMap tm/*M=this.I*/) {
    	this.treeMap = tm;
    	expectedModCount = treeMap.getModCount();
        next = treeMap.firstEntry();
    }

    // Used by SubMapEntryIterator
    TreeMap_PrivateEntryIterator(TreeMap tm/*M=this.I*/, TreeMap_Entry first/*F=this.I*/) {
    	this.treeMap = tm;
    	expectedModCount = treeMap.getModCount();
        next = first;
    }

    public boolean hasNext() {
        return next != null;
    }

    final TreeMap_Entry/*F=this.I*/ nextEntry() {
        if (next == null)
            throw new NoSuchElementException();
        if (treeMap.getModCount() != expectedModCount)
            throw new ConcurrentModificationException();
        lastReturned = next;
        next = treeMap.successor(next);
        return lastReturned;
    }

    public void remove() {
        if (lastReturned == null)
            throw new IllegalStateException();
        if (treeMap.getModCount() != expectedModCount)
            throw new ConcurrentModificationException();
        if (lastReturned.getLeft() != null && lastReturned.getRight() != null)
            next = lastReturned;
        treeMap.deleteEntry(lastReturned);
        expectedModCount++;
        lastReturned = null;
    }
}
