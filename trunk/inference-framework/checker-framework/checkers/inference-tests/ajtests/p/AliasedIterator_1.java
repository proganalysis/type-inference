package p;

public class AliasedIterator_1 implements AliasedIterator {
	/*atomicset(I)*/
	
	/*atomic(I)*/Iterator iterator/*I=this.I*/;
	
	public AliasedIterator_1(Iterator/*I=this.I*/ iterator) {
		this.iterator = iterator;
	}

    // @Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

    // @Override
	public Object next() {
		return iterator.next();
	}

    // @Override
	public void remove() {
		iterator.remove();
	}

}
