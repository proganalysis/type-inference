/**
 * 
 */
package p;

import checkers.inference.aj.quals.*;

/*internal*/class AbstractList_SubList_1 implements ListIterator {
	
	/*atomic(I)*/ private final int index;
    /*atomic(I)*/ private final /*Aliased*/ AbstractList_SubList abstractListSubList/*L=this.I*/;
	
    public AbstractList_SubList_1(/*@Aliased*/ AbstractList_SubList as/*L=this.I*/, final int index){
		this.abstractListSubList = as;
		this.index = index;
		i = abstractListSubList.getL().listIterator(index+abstractListSubList.getOffSet());
	}
	
	private ListIterator i/*I=this.I*/;

    public boolean hasNext() {
        return nextIndex() < abstractListSubList.getSize();
    }

    public Object next() {
        if (hasNext())
            return i.next();
        else
            throw new NoSuchElementException();
    }

    public boolean hasPrevious() {
        return previousIndex() >= 0;
    }

    public Object previous() {
        if (hasPrevious())
            return i.previous();
        else
            throw new NoSuchElementException();
    }

    public int nextIndex() {
        return i.nextIndex() - abstractListSubList.getOffSet();
    }

    public int previousIndex() {
        return i.previousIndex() - abstractListSubList.getOffSet();
    }

    public void remove() {
        i.remove();
        abstractListSubList.setExpectedModCount(abstractListSubList.getL().getModCount());
        abstractListSubList.setSize(abstractListSubList.getSize()-1);//abstractListSubList.size--;
        abstractListSubList.incrementModCount();
    }

    public void set(Object o) {
        i.set(o);
    }

    public void add(Object o) {
        i.add(o);
        abstractListSubList.setExpectedModCount(abstractListSubList.getL().getModCount());
        abstractListSubList.setSize(abstractListSubList.getSize()+1); //abstractListSubList.size++;
        abstractListSubList.incrementModCount();
    }
}
