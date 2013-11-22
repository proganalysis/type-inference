/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class TreeMap_1 extends AbstractSet {
	
	/*atomic(L)*/ private final TreeMap treeMap/*M=this.L*/;
	
    public TreeMap_1(/*@Aliased*/ TreeMap tm/*M=this.L*/){
		this.treeMap = tm;
	}
	
	public Iterator/*I=this.L*/ iterator() {
        return (Iterator/*I=this.L*/) new TreeMap_KeyIterator/*I=this.L*/(treeMap);
    }

    public int size() {
        return treeMap.size();
    }

    public boolean contains(Object o) {
        return treeMap.containsKey(o);
    }

    public boolean remove(Object o) {
        int oldSize = treeMap.getSize();
        treeMap.remove(o);
        return treeMap.getSize() != oldSize;
    }

    public void clear() {
    	treeMap.clear();
    }
}
