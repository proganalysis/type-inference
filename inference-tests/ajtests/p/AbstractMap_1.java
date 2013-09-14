/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class AbstractMap_1 extends AbstractSet {
    /*atomic(L)*/ private final /*Aliased*/ AbstractMap abstractMap/*M=this.L*/;
    public AbstractMap_1(/*unitfor(M)*/ /*@Aliased*/ AbstractMap am/*M=this.L*/){
		this.abstractMap = am;
	}
    public /*Aliased*/ Iterator/*I=this.L*/ iterator() {
	    return (Iterator/*I=this.L*/) new AbstractMap_2/*I=this.L*/(abstractMap);
	}

	public int size() {
	    return abstractMap.size();
	}

	public boolean contains(Object k) {
	    return abstractMap.containsKey(k);
	}
}
