package p;

import checkers.inference.aj.quals.*;

/*internal*/class AbstractList_RandomAccessSubList extends AbstractList_SubList implements RandomAccess {
    AbstractList_RandomAccessSubList(/*Aliased*/ AbstractList list/*L=this.L*/, int fromIndex, int toIndex) {
        super(list, fromIndex, toIndex);
    }

    public /*Aliased*/ List/*L=this.L*/ subList(int fromIndex, int toIndex) {
        return (List/*L=this.L*/) new AbstractList_RandomAccessSubList/*L=this.L*/(this, fromIndex, toIndex);
    }
}
