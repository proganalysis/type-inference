/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class HashMap_EntryIterator extends HashMap_HashIterator {
    public HashMap_EntryIterator(/*@Aliased*/ HashMap hm/*M=this.I*/){
		super(hm);
	}
    public Object/*F=this.I*/ next() {
        return nextEntry();
    }
}
