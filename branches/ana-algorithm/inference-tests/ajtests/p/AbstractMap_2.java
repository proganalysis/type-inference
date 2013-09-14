/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class AbstractMap_2 implements Iterator {
    /*atomic(I)*/ private /*Aliased*/ AbstractMap abstractMap/*M=this.I*/;
	/*atomic(I)*/ private Iterator i/*I=this.I*/ = abstractMap.entrySet().iterator();
    public AbstractMap_2(/*@Aliased*/ AbstractMap am/*M=this.I*/){
		this.abstractMap = am;
	}

	public boolean hasNext() {
	    return i.hasNext();
	}

	public Object next() {
	    return ((Map_Entry/*F=this.I*/)i.next()).getKey();
	}

	public void remove() {
	    i.remove();
	}
}
