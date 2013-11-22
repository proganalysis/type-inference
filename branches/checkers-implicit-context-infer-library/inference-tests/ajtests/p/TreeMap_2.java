/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class TreeMap_2 extends AbstractCollection {
	
	/*atomic(L)*/private final TreeMap treeMap/*M=this.L*/;
	
    public TreeMap_2(/*@Aliased*/ TreeMap tm/*M=this.L*/){
		this.treeMap = tm;
	}
	
	public Iterator/*I=this.L*/ iterator() {
        return (Iterator/*I=this.L*/) new TreeMap_ValueIterator/*I=this.L*/(treeMap);
    }

    public int size() {
        return treeMap.size();
    }

    public boolean contains(Object o) {
        for (TreeMap_Entry e/*F=this.L*/ = treeMap.firstEntry(); e != null; e = treeMap.successor(e))
            if (TreeMap.valEquals(e.getValue(), o))
                return true;
        return false;
    }

    public boolean remove(Object o) {
        for (TreeMap_Entry e/*F=this.L*/ = treeMap.firstEntry(); e != null; e = treeMap.successor(e)) {
            if (TreeMap.valEquals(e.getValue(), o)) {
            	treeMap.deleteEntry(e);
                return true;
            }
        }
        return false;
    }

    public void clear() {
    	treeMap.clear();
    }
}
