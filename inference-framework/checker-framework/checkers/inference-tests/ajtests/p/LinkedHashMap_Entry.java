package p;

import checkers.inference.aj.quals.*;

/**
 * LinkedHashMap entry.
 */
/*internal*/class LinkedHashMap_Entry extends HashMap_Entry {
    // These fields comprise the doubly linked list used for iteration.
    /*atomic(F)*/ private LinkedHashMap_Entry before/*F=this.F*/;
    /*atomic(F)*/ private LinkedHashMap_Entry after/*F=this.F*/;

LinkedHashMap_Entry(int hash, Object key, Object value, HashMap_Entry next/*F=this.F*/) {
        super(hash, key, value, next);
    }

	public LinkedHashMap_Entry/*F=this.F*/ getBefore(){
		return before;
	}
	
    public void setBefore(/*@Aliased*/ LinkedHashMap_Entry/*F=this.F*/ e){
		before = e;
	}
	 
	public LinkedHashMap_Entry/*F=this.F*/ getAfter(){
		return after;
	}
	
    public void setAfter(/*@Aliased*/ LinkedHashMap_Entry/*F=this.F*/ e){
		after = e;
	}

    /**
     * Remove this entry from the linked list.
     */
    private void remove() {
        before.after = after;
        after.before = before;
    }

    /**                                             
     * Insert this entry before the specified existing entry in the list.
     */
    void addBefore(LinkedHashMap_Entry existingEntry/*F=this.F*/) {
        after  = existingEntry;
        before = existingEntry.before;
        before.after = this;
        after.before = this;
    }

    /**
     * This method is invoked by the superclass whenever the value
     * of a pre-existing entry is read by Map.get or modified by Map.set.
     * If the enclosing Map is access-ordered, it moves the entry
     * to the end of the list; otherwise, it does nothing. 
     */
    void recordAccess(HashMap m/*M=this.F*/) {
        LinkedHashMap lm/*M=this.F*/ = (LinkedHashMap/*M=this.F*/)m; // FT: add annotation?
        if (lm.getAccessOrder()) {
            lm.setModCount(lm.getModCount());
            remove();
            addBefore(lm.getHeader());
        }
    }

    void recordRemoval(HashMap m/*M=this.F*/) {
        remove();
    }
}
