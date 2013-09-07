/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class AbstractMap_3 extends AbstractCollection {
    /*atomic(L)*/ private final /*Aliased*/ AbstractMap abstractMap/*M=this.L*/;
    public AbstractMap_3(/*@Aliased*/ AbstractMap am/*M=this.L*/){
		this.abstractMap = am;
	}
    public /*Aliased*/ Iterator/*I=this.L*/ iterator() {
	    return (Iterator/*I=this.L*/) new AbstractMap_4/*I=this.L*/(abstractMap);
    }

	public int size() {
	    return abstractMap.size();
	}

	public boolean contains(Object v) {
	    return abstractMap.containsValue(v);
	}
}
