/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class TreeMap_ValueIterator extends TreeMap_PrivateEntryIterator {
    public TreeMap_ValueIterator(/*@Aliased*/ TreeMap tm/*M=this.I*/){
		super(tm);
	}
    public Object next() {
        return nextEntry().getValue();
    }
}
