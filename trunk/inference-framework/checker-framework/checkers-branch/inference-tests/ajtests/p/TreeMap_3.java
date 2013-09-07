/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class TreeMap_3 extends AbstractSet {
	
	/*atomic(L)*/private final TreeMap treeMap/*M=this.L*/;
	
    public TreeMap_3(/*@Aliased*/ TreeMap tm/*M=this.L*/){
		this.treeMap = tm;
	}
	
	public Iterator/*I=this.L*/ iterator() {
        return (Iterator/*I=this.L*/) new TreeMap_EntryIterator/*I=this.L*/(treeMap);
    }

    public boolean contains(Object o) {
        if (!(o instanceof Map_Entry))
            return false;
        Map_Entry entry/*F=this.L*/ = (Map_Entry/*F=this.L*/) o;
        Object value = entry.getValue();
        TreeMap_Entry p/*F=this.L*/ = treeMap.getEntry(entry.getKey());
        return p != null && TreeMap.valEquals(p.getValue(), value);
    }

    public boolean remove(Object o) {
        if (!(o instanceof Map_Entry))
            return false;
        Map_Entry entry/*F=this.L*/ = (Map_Entry/*F=this.L*/) o;
        Object value = entry.getValue();
        TreeMap_Entry p/*F=this.L*/ = treeMap.getEntry(entry.getKey());
        if (p != null && TreeMap.valEquals(p.getValue(), value)) {
        	treeMap.deleteEntry(p);
            return true;
        }
        return false;
    }

    public int size() {
        return treeMap.size();
    }

    public void clear() {
    	treeMap.clear();
    }
}
