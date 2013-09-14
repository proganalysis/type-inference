/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class TreeMap_KeyIterator extends TreeMap_PrivateEntryIterator {
    public TreeMap_KeyIterator(/*@Aliased*/ TreeMap tm/*M=this.I*/){
		super(tm);
	}
    public Object next() {
        return nextEntry().getKey();
    }
}
