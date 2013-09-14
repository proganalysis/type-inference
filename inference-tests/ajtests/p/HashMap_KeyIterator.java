/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class HashMap_KeyIterator extends HashMap_HashIterator {
    public HashMap_KeyIterator(/*@Aliased*/ HashMap hm/*M=this.I*/){
		super(hm);
	}
    public Object next() {
        return nextEntry().getKey();
    }
}
