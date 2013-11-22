/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class AbstractList_Itr implements Iterator {
		
	
    public AbstractList_Itr(/*@Aliased*/ AbstractList al/*L=this.I*/){
	this.abstractList = al;
	expectedModCount = abstractList.getModCount();
}

    /*atomic(I)*/ private final /*Aliased*/ AbstractList abstractList/*L=this.I*/;

/**
 * Index of element to be returned by subsequent call to next.
 */
/*atomic(I)*/ private int cursor = 0;

/**
 * Index of element returned by most recent call to next or
 * previous.  Reset to -1 if this element is deleted by a call
 * to remove.
 */
/*atomic(I)*/ private int lastRet = -1;

/**
 * The modCount value that the iterator believes that the backing
 * List should have.  If this expectation is violated, the iterator
 * has detected concurrent modification.
 */
/*atomic(I)*/ private int expectedModCount;

public int getLastRet(){
	return lastRet;
}

public void setLastRet(int l){
	lastRet = l;
}

public int getCursor(){
	return cursor;
}

public int setCursor(int c){
	cursor = c;
	return cursor;
}

public void setExpectedModCount(int e){
	expectedModCount = e;
}

public AbstractList/*L=this.I*/ getAbstractList(){
	return (AbstractList/*L=this.I*/) abstractList;
}

public boolean hasNext() {
        return cursor != abstractList.size();
}

public Object next() {
        checkForComodification();
    try {
	Object next = abstractList.get(cursor);
	lastRet = cursor++;
	return next;
    } catch(IndexOutOfBoundsException e) {
	checkForComodification();
	throw new NoSuchElementException();
    }
}

public void remove() {
    if (lastRet == -1)
	throw new IllegalStateException();
        checkForComodification();

    try {
    	abstractList.remove(lastRet);
	if (lastRet < cursor)
	    cursor--;
	lastRet = -1;
	expectedModCount = abstractList.getModCount();
    } catch(IndexOutOfBoundsException e) {
	throw new ConcurrentModificationException();
    }
}

final void checkForComodification() {
    if (abstractList.getModCount() != expectedModCount)
	throw new ConcurrentModificationException();
}
}
