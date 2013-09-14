/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class TreeMap_EntrySetView extends AbstractSet {
	
	/*atomic(L)*/ private final TreeMap_SubMap treeMapSubMap/*M=this.L*/;
	
    public TreeMap_EntrySetView(/*@Aliased*/ TreeMap_SubMap tmsm/*M=this.L*/){
		treeMapSubMap = tmsm;
	}
	
	/*atomic(L)*/ private transient int size = -1;
	/*atomic(L)*/ private transient int sizeModCount;

    public int size() {
        if (size == -1 || sizeModCount != treeMapSubMap.getTreeMap().getModCount()) {
            size = 0;  sizeModCount = treeMapSubMap.getTreeMap().getModCount();
            Iterator i/*I=this.L*/ = iterator();
            while (i.hasNext()) {
                size++;
                i.next();
            }
        }
        return size;
    }

    public boolean isEmpty() {
        return !iterator().hasNext();
    }

    public boolean contains(Object o) {
        if (!(o instanceof Map_Entry))
            return false;
        Map_Entry entry/*F=this.L*/ = (Map_Entry/*F=this.L*/) o;
        Object key = entry.getKey();
        if (!treeMapSubMap.inRange(key))
            return false;
        TreeMap_Entry node/*F=this.L*/ = treeMapSubMap.getTreeMap().getEntry(key);
        return node != null &&
               TreeMap.valEquals(node.getValue(), entry.getValue());
    }

    public boolean remove(Object o) {
        if (!(o instanceof Map_Entry))
            return false;
        Map_Entry entry/*F=this.L*/ = (Map_Entry/*F=this.L*/) o;
        Object key = entry.getKey();
        if (!treeMapSubMap.inRange(key))
            return false;
        TreeMap_Entry node/*F=this.L*/ = treeMapSubMap.getTreeMap().getEntry(key);
        if (node!=null && TreeMap.valEquals(node.getValue(),entry.getValue())){
        	treeMapSubMap.getTreeMap().deleteEntry(node);
            return true;
        }
        return false;
    }

    public Iterator/*I=this.L*/ iterator() {
        return (Iterator/*I=this.L*/) new TreeMap_SubMapEntryIterator/*I=this.L*/(treeMapSubMap.getTreeMap(),
            (treeMapSubMap.getFromStart() ? treeMapSubMap.getTreeMap().firstEntry() : treeMapSubMap.getTreeMap().getCeilEntry(treeMapSubMap.getFromKey())),
            (treeMapSubMap.getToEnd()     ? null         : treeMapSubMap.getTreeMap().getCeilEntry(treeMapSubMap.getToKey())));
    }
}
