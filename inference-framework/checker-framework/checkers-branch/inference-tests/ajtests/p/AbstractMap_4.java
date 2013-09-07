/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class AbstractMap_4 implements Iterator {
    /*atomic(I)*/ private final /*Aliased*/ AbstractMap abstractMap/*M=this.I*/;
	/*atomic(I)*/ private Iterator i/*I=this.I*/;
    public AbstractMap_4(/*@Aliased*/ AbstractMap am/*M=this.I*/){
		this.abstractMap = am;
		i = abstractMap.entrySet().iterator();
	}

	public boolean hasNext() {
	    return i.hasNext();
	}

	public Object next() {
	    return ((Map_Entry/*F=this.I*/)i.next()).getValue();
	}

	public void remove() {
	    i.remove();
	}
}
