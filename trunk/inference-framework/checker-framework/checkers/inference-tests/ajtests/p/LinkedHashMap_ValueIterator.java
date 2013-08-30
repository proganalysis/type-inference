/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class LinkedHashMap_ValueIterator extends LinkedHashMap_LinkedHashIterator {
		
    public LinkedHashMap_ValueIterator(/*@Aliased*/ LinkedHashMap lhm/*M=this.I*/, /*@Aliased*/ LinkedHashMap_Entry h/*F=this.I*/){
	super(lhm, h);
}
	
public Object next() { return nextEntry().getValue(); }
}
