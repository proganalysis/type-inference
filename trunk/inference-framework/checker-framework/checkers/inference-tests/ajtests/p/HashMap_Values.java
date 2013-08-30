/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class HashMap_Values extends AbstractCollection {
    /**
	 * 
	 */
    /*atomic(L)*/ private final /*Aliased*/ HashMap hashMap/*M=this.L*/;
	/**
	 * @param map
	 */
	HashMap_Values(HashMap map/*M=this.L*/) {
		hashMap = map;
	}
	public Iterator/*I=this.L*/ iterator() {
        return hashMap.newValueIterator();
    }
    public int size() {
        return hashMap.getSize();
    }
    public boolean contains(Object o) {
        return hashMap.containsValue(o);
    }
    public void clear() {
        hashMap.clear();
    }
}
