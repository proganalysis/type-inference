/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class TreeMap_EntryIterator extends TreeMap_PrivateEntryIterator {
    public TreeMap_EntryIterator(/*@Aliased*/ TreeMap tm/*M=this.I*/){
		super(tm);
	}
    public Object /*F=this.I*/ next() {
        return nextEntry();
    }
}
