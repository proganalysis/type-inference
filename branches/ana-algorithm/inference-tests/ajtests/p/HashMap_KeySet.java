/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class HashMap_KeySet extends AbstractSet {
    /**
	 * 
	 */
    /*atomic(L)*/ private final /*Aliased*/ HashMap hashMap/*M=this.L*/;
	/**
	 * @param map
	 */
	HashMap_KeySet(HashMap map/*M=this.L*/) {
		hashMap = map;
	}
	public Iterator/*I=this.L*/ iterator() {
        return hashMap.newKeyIterator();
    }
    public int size() {
        return hashMap.getSize();
    }
    public boolean contains(Object o) {
        return hashMap.containsKey(o);
    }
    public boolean remove(Object o) {
        return hashMap.removeEntryForKey(o) != null;
    }
    public void clear() {
        hashMap.clear();
    }
}
