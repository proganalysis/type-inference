/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class LinkedHashMap_KeyIterator extends LinkedHashMap_LinkedHashIterator {
	
    public LinkedHashMap_KeyIterator(/*@Aliased*/ LinkedHashMap lhm/*M=this.I*/, /*@Aliased*/ LinkedHashMap_Entry h/*F=this.I*/){
	super(lhm, h);
}
	
public Object next() { return nextEntry().getKey(); }
}
