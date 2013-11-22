/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class HashMap_ValueIterator extends HashMap_HashIterator {
    public HashMap_ValueIterator(/*@Aliased*/ HashMap hm/*M=this.I*/){
		super(hm);
	}
    public Object next() {
        return nextEntry().getValue();
    }
}
