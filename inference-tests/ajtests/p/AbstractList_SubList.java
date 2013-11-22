package p;

import checkers.inference.aj.quals.*;

/*internal*/class AbstractList_SubList extends AbstractList {
    /*atomic(L)*/ private /*Aliased*/ AbstractList l/*L=this.L*/;
    /*atomic(L)*/ private int offset;
    /*atomic(L)*/ private int size;
    /*atomic(L)*/ private int expectedModCount;

    public int getSize(){
    	return size;
    }
    
    public void setSize(int s){
    	size = s;
    }
    
    public int getOffSet(){
    	return offset;
    }
    
    public void setExpectedModCount(int e){
    	expectedModCount = e;
    }
    
    public /*Aliased*/ AbstractList/*L=this.L*/ getL(){
    	return (AbstractList/*L=this.L*/) l;
    }
    
    AbstractList_SubList(AbstractList list/*L=this.L*/, int fromIndex, int toIndex) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > list.size())
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                               ") > toIndex(" + toIndex + ")");
        l = list;
        offset = fromIndex;
        size = toIndex - fromIndex;
        expectedModCount = l.getModCount();
    }

    public Object set(int index, Object element) {
        rangeCheck(index);
        checkForComodification();
        return l.set(index+offset, element);
    }

    public Object get(int index) {
        rangeCheck(index);
        checkForComodification();
        return l.get(index+offset);
    }

    public int size() {
        checkForComodification();
        return size;
    }

    public void add(int index, Object element) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException();
        checkForComodification();
        l.add(index+offset, element);
        expectedModCount = l.getModCount();
        size++;
        incrementModCount();
    }

    public Object remove(int index) {
        rangeCheck(index);
        checkForComodification();
        Object result = l.remove(index+offset);
        expectedModCount = l.getModCount();
        size--;
        incrementModCount();
        return result;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        checkForComodification();
        l.removeRange(fromIndex+offset, toIndex+offset);
        expectedModCount = l.getModCount();
        size -= (toIndex-fromIndex);
        incrementModCount();
    }

    public boolean addAll(/*unitfor(L)*/ /*@Aliased*/ Collection/*L=this.L*/ c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, /*unitfor(L)*/ /*@Aliased*/ Collection/*L=this.L*/ c) {
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException(
                "Index: "+index+", Size: "+size);
        int cSize = c.size();
        if (cSize==0)
            return false;

        checkForComodification();
        l.addAll(offset+index, c);
        expectedModCount = l.getModCount();
        size += cSize;
        incrementModCount();
        return true;
    }

    public /*Aliased*/ Iterator iterator() {
        return listIterator();
    }

    public /*Aliased*/ ListIterator/*I=this.L*/ listIterator(final int index) {
        checkForComodification();
        if (index<0 || index>size)
            throw new IndexOutOfBoundsException(
                "Index: "+index+", Size: "+size);

        return (ListIterator/*I=this.L*/) new AbstractList_SubList_1/*I=this.L*/(this, index);
    }
    
    public /*Aliased*/ List/*L=this.L*/ subList(int fromIndex, int toIndex) { 
        return (List/*L=this.L*/) new AbstractList_SubList/*L=this.L*/(this, fromIndex, toIndex);
    }

    private void rangeCheck(int index) {
        if (index<0 || index>=size)
            throw new IndexOutOfBoundsException("Index: "+index+
                                                ",Size: "+size);
    }

    private void checkForComodification() {
        if (l.getModCount() != expectedModCount)
            throw new ConcurrentModificationException();
    }
}
