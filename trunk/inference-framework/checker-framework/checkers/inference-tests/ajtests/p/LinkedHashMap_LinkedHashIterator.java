/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/abstract class LinkedHashMap_LinkedHashIterator implements Iterator {

/*atomic(I)*/ private final LinkedHashMap linkedHashMap/*M=this.I*/;
/*atomic(I)*/ private final LinkedHashMap_Entry header/*F=this.I*/;	// FT: introduced to avoid direct field acc

	
    public LinkedHashMap_LinkedHashIterator(/*@Aliased*/ LinkedHashMap lhm/*M=this.I*/, /*@Aliased*/ LinkedHashMap_Entry h/*F=this.I*/){
	this.header = h;
	linkedHashMap = lhm;
	nextEntry    = header.getAfter();
	expectedModCount = linkedHashMap.getModCount();
}
	
/*atomic(I)*/ private LinkedHashMap_Entry nextEntry/*F=this.I*/;
/*atomic(I)*/ private LinkedHashMap_Entry lastReturned/*F=this.I*/ = null;

/**
 * The modCount value that the iterator believes that the backing
 * List should have.  If this expectation is violated, the iterator
 * has detected concurrent modification.
 */
/*atomic(I)*/ private int expectedModCount;

public boolean hasNext() {
        return nextEntry != header;
}

public void remove() {
    if (lastReturned == null)
	throw new IllegalStateException();
    if (linkedHashMap.getModCount() != expectedModCount)
	throw new ConcurrentModificationException();

    linkedHashMap.remove(lastReturned.getKey());
        lastReturned = null;
        expectedModCount = linkedHashMap.getModCount();
}

LinkedHashMap_Entry/*F=this.I*/ nextEntry() {
    if (linkedHashMap.getModCount() != expectedModCount)
	throw new ConcurrentModificationException();
        if (nextEntry == header)
            throw new NoSuchElementException();

        LinkedHashMap_Entry e/*F=this.I*/ = lastReturned = nextEntry;
        nextEntry = e.getAfter();
        return e;
}
}
