/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/abstract class HashMap_HashIterator implements Iterator {
    /*atomic(I)*/ private HashMap_Entry next/*F=this.I*/;	// next entry to return
    /*atomic(I)*/ private int expectedModCount;	// For fast-fail 
    /*atomic(I)*/ private int index;		// current slot 
    /*atomic(I)*/ private HashMap_Entry current/*F=this.I*/;	// current entry
    /*atomic(I)*/ private final /*Aliased*/ HashMap hashMap/*M=this.I*/;
    
    HashMap_HashIterator(/*Aliased*/HashMap/*M=this.I*/ hm) {
    	hashMap = hm;
        expectedModCount = hashMap.getModCount();
        HashMap_Entry[] t/*this.I[]F=this.I*/ = hashMap.getTable();
        int i = t.length;
        HashMap_Entry n/*F=this.I*/ = null;
        if (hashMap.getSize() != 0) { // advance to first entry
            while (i > 0 && (n = t[--i]) == null)
                ;
        } 
        next = n;   
        index = i; 
    }

    public boolean hasNext() {
        return next != null;
    }

    HashMap_Entry/*F=this.I*/ nextEntry() { 
        if (hashMap.getModCount() != expectedModCount)
            throw new ConcurrentModificationException();
        HashMap_Entry e/*F=this.I*/ = next;
        if (e == null) 
            throw new NoSuchElementException();
            
        HashMap_Entry n/*F=this.I*/ = e.getNext();
        HashMap_Entry[] t/*this.I[]F=this.I*/  = hashMap.getTable();
        int i = index;
        while (n == null && i > 0)
            n = t[--i];
        index = i;
        next = n;
        return current = e;
    }

    public void remove() {
        if (current == null)
            throw new IllegalStateException();
        if (hashMap.getModCount() != expectedModCount)
            throw new ConcurrentModificationException();
        Object k = current.getKey();
        current = null;
        hashMap.removeEntryForKey(k);
        expectedModCount = hashMap.getModCount();
    }

}
