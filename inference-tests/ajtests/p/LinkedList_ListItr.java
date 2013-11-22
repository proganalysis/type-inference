/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class LinkedList_ListItr implements ListIterator {
	
    /*atomic(I)*/ private final /*Aliased*/ LinkedList linkedList/*L=this.I*/;	
	/*atomic(I)*/ private final LinkedList_Entry header/*E=this.I*/;
	/*atomic(I)*/ private LinkedList_Entry lastReturned/*E=this.I*/;
	/*atomic(I)*/ private LinkedList_Entry next/*E=this.I*/;
	/*atomic(I)*/ private int nextIndex;
	/*atomic(I)*/ private int expectedModCount;

    LinkedList_ListItr(LinkedList ll/*L=this.I*/,LinkedList_Entry h/*E=this.I*/, int index) {
	header = h;
	this.linkedList = ll;
	lastReturned = header;
	expectedModCount = linkedList.getModCount();
    if (index < 0 || index > linkedList.getSize())
	throw new IndexOutOfBoundsException("Index: "+index+
					    ", Size: "+linkedList.getSize());
    if (index < (linkedList.getSize() >> 1)) {
	next = header.next;
	for (nextIndex=0; nextIndex<index; nextIndex++)
	    next = next.next;
    } else {
	next = header;
	for (nextIndex=linkedList.getSize(); nextIndex>index; nextIndex--)
	    next = next.previous;
    }
}

public boolean hasNext() {
    return nextIndex != linkedList.getSize();
}

public Object next() {
    checkForComodification();
    if (nextIndex == linkedList.getSize())
	throw new NoSuchElementException();

    lastReturned = next;
    next = next.next;
    nextIndex++;
    return lastReturned.element;
}

public boolean hasPrevious() {
    return nextIndex != 0;
}

public Object previous() {
    if (nextIndex == 0)
	throw new NoSuchElementException();

    lastReturned = next = next.previous;
    nextIndex--;
    checkForComodification();
    return lastReturned.element;
}

public int nextIndex() {
    return nextIndex;
}

public int previousIndex() {
    return nextIndex-1;
}

public void remove() {
        checkForComodification();
        LinkedList_Entry lastNext/*E=this.I*/ = lastReturned.next;
        try {
        	linkedList.remove(lastReturned);
        } catch (NoSuchElementException e) {
            throw new IllegalStateException();
        }
    if (next==lastReturned)
            next = lastNext;
        else
	nextIndex--;
    lastReturned = header;
    expectedModCount++;
}

public void set(Object o) {
    if (lastReturned == header)
	throw new IllegalStateException();
    checkForComodification();
    lastReturned.element = o;
}

public void add(Object o) {
    checkForComodification();
    lastReturned = header;
    linkedList.addBefore2(o, next);
    nextIndex++;
    expectedModCount++;
}

final void checkForComodification() {
    if (linkedList.getModCount() != expectedModCount)
	throw new ConcurrentModificationException();
}
}
