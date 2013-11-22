/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class HashMap_EntrySet extends AbstractSet/*<Map.Entry>*/ {
    /**
	 * 
	 */
    /*atomic(L)*/ private final /*Aliased*/ HashMap hashMap/*M=this.L*/;
	/**
	 * @param map
	 */
    HashMap_EntrySet(/*unitfor(M)*/ /*@Aliased*/ HashMap map/*M=this.L*/) {
		hashMap = map;
	}
	public Iterator/*I=this.L*//*<Map.Entry>*/ iterator() {
        return hashMap.newEntryIterator();
    }
    public boolean contains(Object o) {
        if (!(o instanceof Map_Entry))
            return false;
        Map_Entry e/*F=this.L*/ = (Map_Entry/*F=this.L*/) o;
        HashMap_Entry candidate/*F=this.L*/ = hashMap.getEntry(e.getKey());
        return candidate != null && candidate.equals(e);
    }
    public boolean remove(Object o) {
        return hashMap.removeMapping(o) != null;
    }
    public int size() {
        return hashMap.getSize();
    }
    public void clear() {
        hashMap.clear();
    }
}
