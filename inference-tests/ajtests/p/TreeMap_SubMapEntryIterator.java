/**
 * 
 */
package p;

/*internal*/class TreeMap_SubMapEntryIterator extends TreeMap_PrivateEntryIterator {
    /*atomic(I)*/ private final Object firstExcludedKey;

    TreeMap_SubMapEntryIterator(TreeMap tm/*M=this.I*/, TreeMap_Entry first/*F=this.I*/, TreeMap_Entry firstExcluded/*F=this.I*/) {
        super(tm, first);
        firstExcludedKey = (firstExcluded == null
			? null
			: firstExcluded.getKey());
    }

    public boolean hasNext() {
        return getNext() != null && getNext().getKey() != firstExcludedKey;
    }

    public Object/*F=this.I*/ next() {
        if (getNext() == null || getNext().getKey() == firstExcludedKey)
            throw new NoSuchElementException();
        return nextEntry();
    }
}
