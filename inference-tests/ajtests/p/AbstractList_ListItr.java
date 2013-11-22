/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class AbstractList_ListItr extends AbstractList_Itr implements ListIterator {
    AbstractList_ListItr(/*Aliased*/ AbstractList al/*L=this.I*/, int index) {
	super(al);
    setCursor(index);
}

public boolean hasPrevious() {
    return getCursor() != 0;
}

    public Object previous() {
        checkForComodification();
        try {
            int i = getCursor() - 1;
            Object previous = getAbstractList().get(i);
            setLastRet(i); setCursor(i);
            return previous;
        } catch(IndexOutOfBoundsException e) {
            checkForComodification();
            throw new NoSuchElementException();
        }
    }

public int nextIndex() {
    return getCursor();
}

public int previousIndex() {
    return getCursor()-1;
}

public void set(Object o) {
    if (getLastRet() == -1)
	throw new IllegalStateException();
        checkForComodification();

    try {
    	getAbstractList().set(getLastRet(), o);
	setExpectedModCount(getAbstractList().getModCount());
    } catch(IndexOutOfBoundsException e) {
	throw new ConcurrentModificationException();
    }
}

public void add(Object o) {
        checkForComodification();

    try {
    	getAbstractList().add(setCursor(getCursor()+1), o);
	setLastRet(-1);
	setExpectedModCount(getAbstractList().getModCount());
    } catch(IndexOutOfBoundsException e) {
	throw new ConcurrentModificationException();
    }
}
}
