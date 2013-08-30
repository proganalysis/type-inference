/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class LinkedHashMap_EntryIterator extends LinkedHashMap_LinkedHashIterator {
	
    public LinkedHashMap_EntryIterator(/*@Aliased*/ LinkedHashMap lhm/*M=this.I*/, /*@Aliased*/ LinkedHashMap_Entry h/*F=this.I*/){
    	super(lhm, h);
    }	
	
public Object/*F=this.I*/ next() { return nextEntry(); }
}
